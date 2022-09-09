**rankmirrors**
comes with the [pacman-contrib][alps-pacman-contrib] package on the Arch Linux
distribution. It is a simple utility for ranking a list of servers by their
response-times. Its input is usually an [ArchLinux MirrorList
file][alps-mirrorlist-gen] and the output is determined by a set of
command-line options.

Because of the simple nature of the goal, I am using this as an exercise in
various programming languages. The original utility is [written in
bash][gitlab-pacman-contrib-rankmirrors].

The idea is to replicate functionality as closely as possible, but I may add in
some ~~bugs~~ features here and there.

[alps-pacman-contrib]: https://archlinux.org/packages/community/x86_64/pacman-contrib/
[alps-mirrorlist-gen]: https://archlinux.org/mirrorlist/
[gitlab-pacman-contrib-rankmirrors]: https://gitlab.archlinux.org/pacman/pacman-contrib/-/blob/master/src/rankmirrors.sh.in
