# Local setup

Make sure the github-pages version in [Gemfile](Gemfile) matches the version listed [here](https://pages.github.com/versions/).

Install [rbenv](https://github.com/rbenv/rbenv).

Run the following commands after substituting the ruby version listed [here](https://pages.github.com/versions/).

```shell
ruby_version="0.0.0"
rbenv install "${ruby_version}"
rbenv local "${ruby_version}"
gem install bundler
bundle install
bundle exec jekyll serve --drafts
```
