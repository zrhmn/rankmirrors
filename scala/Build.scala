//> using scala "3.1.2"
//> using resourceDir "./resources"
//> using option "-deprecation"
//> using lib "com.monovore::decline:2.3.0"
//> using lib "com.monovore::decline-effect:2.2.0"
//> using lib "co.fs2::fs2-core:3.2.12"

package rankmirrors

object buildInfo:
    val name    = "rankmirrors"
    val desc    = "rank a list of mirror servers by response-time"
    val version = "0.0.1"
