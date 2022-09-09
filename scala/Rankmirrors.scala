package rankmirrors

import cats.implicits.*
import cats.effect.{IO, ExitCode}

import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp

object Rankmirrors
    extends CommandIOApp(
      name = buildInfo.name,
      header = buildInfo.desc,
      version = buildInfo.version,
    ):
    final override def main: Opts[IO[ExitCode]] = Opts.unit.map { _ =>
        IO {
            println("I am sentient.")
            ExitCode.Success
        }
    }

/** mirror ---------------------------------------------------------------- */
package object mirror:
    import java.net.URL
    import scala.concurrent.duration.*
    import fs2.text.*

    private final case class Mirror private (
        val url: URL,
        val label: Option[String],
        val comment: Option[String],
        val responseTime: Option[Duration],
        private val varargs: AnyVal*,
    )

/** parser ---------------------------------------------------------------- */
package object parser:
    import java.net.URL
    import scala.collection.immutable.*
    import scala.util.Try

    import fs2.{Stream, Pipe}
    import fs2.text.*

    /** Line represents a line of text from canonical mirrorlist source. */
    private enum Line:
        case Empty
        case Comment(text: String)
        case Server(url: URL)

    object Line:
        private final case class Error(private val src: String)
            extends Throwable:
            override def toString = s"Bad input: $src"

        object Server:
            private val regex = """^Server\s*=\s*(.*)$""".r
            def apply(src: String): Either[Throwable, Line] = src match
                case regex(url) => Try(URL(url)).map(Server(_)).toEither
                case _          => Left(Error(src))

        object Comment:
            def apply(src: String): Either[Throwable, Line] = Line
                .Comment(src drop 1)

        private val serverRegex = """^Server\s*=\s*(.*)$""".r

        def apply(src: String): Either[Throwable, Line] = src match
            case serverRegex(url)       => Line.Server(url)
            case s if s.startsWith("#") => Line.Comment(s)
            case _ if src.isEmpty       => Right(Line.Empty)
            case _                      => Left(new Throwable)

        def decodePipe[F[_]]: Pipe[F, Byte, Either[Throwable, Line]] =
            byteStream =>
                byteStream.through(utf8.decode).through(lines).map(_.trim)
                    .filter(_.nonEmpty).map(Line(_))

    private sealed trait LineSeq(it: Seq[Line])
        extends Seq[Line] with SeqOps[Line, Seq, Seq[Line]]:
        final override def apply(i: Int) = it(i)
        final override def iterator      = it.iterator
        final override def length        = it.length

    /** Section represents multiple lines from a mirrorlist containing _at
      * most_ one `Line.Server`.
      */
    private final case class Section(lines: Line*) extends LineSeq(lines)

    object Section:
        def empty = Section()

        def decode[F[_]](stream: Stream[F, Byte]): Either[Throwable, Section] =
            def sectionBoundary = (_: Either[Throwable, Line]) match
                case Left(_) => false
                case Right(line) => line match
                        case Line.Empty => false
                        case Line.Comment(text) => Line.Server(text) match
                                case Left(_)  => false
                                case Right(_) => true
                        case Line.Server(_) => true

            def errOrSection = stream.through(Line.decodePipe)
                .takeThrough(sectionBoundary)

            ???
