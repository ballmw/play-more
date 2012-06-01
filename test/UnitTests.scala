import java.util.Date

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class UnitTests extends Specification {

  import models._
  import anorm._

  "Athlete" should {
    "create and retrieve itself" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        var user = Athlete(NotAssigned, "jim@gmail.com", "secret", "Jim", "Smith")
        Athlete.create(user)

        val jim = Athlete.find("email", "jim@gmail.com").head

        jim must not be equalTo(None)
        jim.firstName must_== ("Jim")
      }
    }

    "login" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Athlete.create(Athlete(NotAssigned, "bob@gmail.com", "secret", "Bob", "Johnson"))

        Athlete.connect("bob@gmail.com", "secret") must_!= (None)
        Athlete.connect("bob@gmail.com", "badpassword") must_== (None)
        Athlete.connect("tom@gmail.com", "secret") must be_==(None)
      }
    }
  }

  "Workout" should {
    "create a Workout" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Athlete.create(Athlete(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
        Workout.create(Workout(NotAssigned, "My first run", "With a hangover", 4, 38, new Date, 1))

        Workout.count() must be_==(1)

        val workouts = Workout.find("athleteId", "1")

        workouts.length must be_==(1)

        val firstWorkout = workouts.headOption

        firstWorkout must not be_== (None)
        firstWorkout.get.athleteId must be_==(1)
        firstWorkout.get.title must be_==("My first run")
        firstWorkout.get.description must be_==("With a hangover")
      }
    }

    "retrieve Workouts by athlete" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Athlete.create(Athlete(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
        Workout.create(Workout(NotAssigned, "My 1st workout", "Yee Haw!", 3.20, 70, new Date, 1))

        val workouts = Workout.allWithAthlete

        workouts.length must be_==(1)

        val (workout, user) = workouts.head

        workout.title must be_==("My 1st workout")
        user.firstName must be_==("Bob")
      }
    }

    "support Comments" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Athlete.create(Athlete(Id(1), "bob@gmail.com", "secret", "Bob", "Johnson"))
        Workout.create(Workout(Id(1), "My first workout", "Yee Haw!", 2, 10, new Date, 1))
        Comment.create(Comment(NotAssigned, "Jim", "Nice workout!", new Date, 1))
        Comment.create(Comment(NotAssigned, "Bryan", "Great weather today!", new Date, 1))

        Athlete.count() must be_==(1)
        Workout.count() must be_==(1)
        Comment.count() must be_==(2)

        /*val ((workout, athlete), comments) = Workout.byIdWithAthleteAndComments(1)

        workout.title must be_==("My first workout")
        athlete.firstName must be_==("Bob")
        comments.length must be_==(2)
        comments(0).author must be_==("Jim")
        comments(1).author must be_==("Bryan")*/
      }
    }
  }

  "Global" should {
    "load seed data into DB" in {
      running(FakeApplication()) {

        InitialData.insert()

        Athlete.count() must be_==(2)
        Workout.count() must be_==(3)
        Comment.count() must be_==(3)

        Athlete.connect("mraible@gmail.com", "beer") should not be (None)
        Athlete.connect("trishmcginity@gmail.com", "whiskey") should not be (None)
        Athlete.connect("trishmcginity@gmail.com", "badpassword") must be_==(None)
        Athlete.connect("fred@gmail.com", "secret") must be_==(None)

        val allWorkoutsWithAthleteAndComments = Workout.allWithAthleteAndComments

        allWorkoutsWithAthleteAndComments must be_==(3)

        val ((workout, athlete), comments) = allWorkoutsWithAthleteAndComments(1)
        workout.title must be_==("Monarch Lake Trail")
        athlete.firstName must be_==("Matt")
        comments.length must be_==(2)

        // We have a referential integrity error
        /*evaluating {
        Athlete.delete("email={email}")
          .on("email" -> "mraible@gmail.com").executeUpdate()
      } should produce[java.sql.SQLException]*/

        Workout.delete(1) must be_==(2)

        Athlete.delete(1) must be_==(1)

        Athlete.count() must be_==(1)
        Workout.count() must be_==(1)
        Comment.count() must be_==(0)
      }
    }
  }
}