# Wikidata-Toolkit's governance model

This document describes the different roles for participants to the Wikidata-Toolkit project.

## Roles

At any point in time, a person is either a **Contributor**, a **Members** or a **Owner**.
The list of Members and Owners is recorded in `roles.yml` in this repository.

The contents of `roles.yml` is mirrored by the [GitHub teams of the Wikidata-Toolkit organization](https://github.com/Wikidata-Toolkit). (Owners are responsible for such updates).
All project members and owners are made public in the GitHub organization.

## Rights and responsibilities

* Contributors can use Wikidata-Toolkit and make changes to it as specified by the license.
* Members can merge pull requests made by others, publish new releases and triage issues, in addition to the above. They are responsible for processing the pull request and issue backlog.
* Owners can administer the GitHub organization and repository, following the principles outlined in this document, in addition to the above.

## Onboarding

A Contributor can apply to become a Member by opening a pull request making the corresponding change in `roles.yml`. The pull request is merged or rejected following a vote open to existing Members and Owners.
A Member can apply to become an Owner by opening a pull request making the corresponding change in `roles.yml`. The pull request is merged or rejected following a vote open to existing Owners.

## Offboarding

A Member can step down to being a Contributor and an Owner can step down to being a Member by making the appropriate pull request. The pull request is accepted without a vote if it is opened by the person subject to the change.
A Member who hasn't made use of Member rights for 3 years automatically becomes a Contributor.
A Owner who hasn't made use of Member or Owner rights for 2 years and isn't the only Owner automatically becomes a Member.

## Voting procedure

Votes are held in pull requests which change this document or `roles.yml` and are not covered by the exceptions mentioned above. Eligible voters can cast three votes:
* Support, +1
* Accept, 0
* Reject, -3

Votes are cast publicly by commenting on the pull request (an "Approve" review is understood as a "Support", a "Request changes" review is understood as a "Reject"). The vote passes if the sum of votes is nonnegative. Votes last for at least three days
unless all eligible votes have been cast.

## Making changes to this document

Changes to this document are made by pull requests, where a vote open to Members and Owners is held. The person proposing the change is able to cast a vote.

## Miscellaneous

This document is inspired from [Mergiraf's governance model](https://codeberg.org/mergiraf/mergiraf/src/branch/main/GOVERNANCE.md).

This document is available under a [CC0 license](https://creativecommons.org/public-domain/cc0/). No rights reserved.
