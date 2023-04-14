# Donut CLI

The donut CLI.

## Commands

``` sh
donut new my-app-name
donut new my-app-name --target-dir dirname
donut new my-app-name --target-dir dirname --overwrite
```

## Development

How it works: the `gen-script` babashka task creates the `donut` file by
concatenating the component scripts.

The file `donut-cli/prelude` contains all the deps needed by subsequent scripts.

Updating the CLI to use the latest single-page-app-template involves:

1. Get the latest SHA for the repo
2. Update `donut-cli/prelude`
3. Publish the `donut-cli` tools with `bb publish`. This will create a new tag.
4. [Download latest donut-cli zip release](https://github.com/donut-party/donut-cli/releases)
5. Run sha256sum v0.0.x.zip to get sum
6. Update homebrew-brew/Formula/donut.rb with the URL for the new release, and the SHA
