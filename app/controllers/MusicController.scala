package controllers

import play.api.mvc._

object MusicController extends Controller {

  def index() = Action { request =>
    Ok(Scalate("/music/index.jade").render(request))
  }
}
