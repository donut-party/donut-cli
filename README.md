# Donut CLI

The donut CLI.

## Commands

``` sh
donut new my-app-name
donut new my-app-name --target-dir dirname
donut new my-app-name --target-dir dirname --overwrite
```

## Development

This repo contains both the CLI and tools for developing the CLI.

To publish a new release on GitHub:

``` shell
./develop/bin/develop publish
```

This will:

1. Bump the version number
2. Update the single-page-app-template dep to the latest SHA on main on GH
3. Commit and push

From there, a GH workflow creates the release.

The CLI is published to homebrew. To update the homebrew formula:

``` shell
./develop/bin/develop update-brew
```

This updates the donut.rb file in the homebrew-brew repo to reflect the latest
release, but it doesn't push the change to GH.
