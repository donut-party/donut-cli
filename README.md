# Donut CLI

The donut CLI.

## Commands

``` sh
donut new my-app-name
donut new my-app-name --target-dir dirname
donut new my-app-name --target-dir dirname --overwrite
```

## Development

Release tooling for the CLI lives in [dough](https://github.com/donut-party/dough), checked out as a sibling of this repo.

To publish a new release on GitHub:

``` shell
dough cli-publish
```

This will:

1. Point the CLI at the newest `party.donut/generate` and `party.donut/generators` on Clojars and the latest single-page-app-template SHA on main
2. Bump the version number
3. Commit, tag, and push

From there, a GH workflow creates the release.

The CLI is published to homebrew. To update the homebrew formula:

``` shell
dough brew-formula
```

This updates the donut.rb file in the homebrew-brew repo to reflect the latest
release, but it doesn't push the change to GH.
