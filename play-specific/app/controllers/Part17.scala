package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Action, Controller, Result }
//
import scala.concurrent.Future
import services.UserService
//
import scalaz._
import scalaz.Scalaz._
import scalaz.std.scalaFuture.futureInstance

class Part17 extends Controller {

  def index = Action.async { request =>
    val data = request.queryString.mapValues(_.head)

    val serviceResult = for {
      username <- UserService.getUserName(data) |> HttpResult.fromOption(BadRequest("Username missing from request"))
      user <- UserService.getUser(username) |> HttpResult.fromFOption(NotFound("User not found"))
      email = UserService.getEmail(user)
      validatedEmail <- UserService.validateEmail(email) |> HttpResult.fromEither(InternalServerError(_))
      success <- UserService.sendEmail(validatedEmail) |> HttpResult.fromFuture
    } yield {
      if(success) Ok("Mail successfully sent!")
      else InternalServerError("Failed to send email :(")
    }

    constructResult(serviceResult)

  }



  // Type alias for our result type
  type HttpResult[A] = EitherT[Future, Result, A]

  // Constructors for our result type
  object HttpResult {
    def point[A](a: A): HttpResult[A] = EitherT(Future.successful(\/-(a)))
    def fromFuture[A](fa: Future[A]): HttpResult[A] = EitherT(fa.map(\/-(_)))
    def fromEither[A](va: Result \/ A): HttpResult[A] = EitherT(Future.successful(va))
    def fromEither[A, B](failure: B => Result)(va: B \/ A): HttpResult[A] = EitherT(Future.successful(va.leftMap(failure)))
    def fromOption[A](failure: Result)(oa: Option[A]): HttpResult[A] = EitherT(Future.successful(oa \/> failure))
    def fromFOption[A](failure: Result)(foa: Future[Option[A]]): HttpResult[A] = EitherT(foa.map(_ \/> failure))
    def fromFEither[A, B](failure: B => Result)(fva: Future[B \/ A]): HttpResult[A] = EitherT(fva.map(_.leftMap(failure)))
  }

  // Converter from our result type to a Play result
  def constructResult(result: HttpResult[Result]) = result.run.map { _.fold(identity, identity) }



}

