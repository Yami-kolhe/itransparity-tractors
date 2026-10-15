# iTransparity Tractors AEM Project

## Overview

This project is an Adobe Experience Manager (AEM) implementation for an iTransparity tractor site. It includes authorable components for the site header, hero banner, tractor listing, tractor detail, store/dealer finder, and footer.

## Main Components

| Component | Purpose | Authoring source |
|---|---|---|
| Site Header | Logo and page navigation, including mobile navigation | `/apps/itransparity/components/header` |
| Hero Banner | Banner image, headline, sub-text and CTA | `/apps/itransparity/components/hero` |
| Tractor Grid | Displays tractor cards from tractor content fragments | `/apps/itransparity/components/tractorgrid` |
| Tractor Detail | Displays a selected tractor content fragment and specifications | `/apps/itransparity/components/tractordetail` |
| Store Finder | Location selection, location detection and dealer results | `/apps/itransparity/components/storefinder` |
| Site Footer | Copyright text | `/apps/itransparity/components/footer` |

## Tractor Content

The tractor grid/detail implementation reads tractor data from Content Fragment data. The tractor detail component can be configured with a `Tractor` fragment path. If the component dialog is left empty, the implementation can fall back to the fragment associated with the page according to the Sling Model logic.

The tractor detail CTA is authored as part of the tractor data and is rendered when both CTA label and CTA link are available.

## Store Finder

The Store Finder uses the `StoreFinderModel` to expose these configured regions:

- London, UK
- Nairobi, Kenya
- Delhi, India
- Other / unlisted

The frontend uses the configured latitude/longitude values to determine the nearest listed region. A 150 km maximum distance is applied; locations beyond that threshold are treated as `Other / unlisted`.

The Store Finder loads dealer data asynchronously from the `StoresServlet`. The servlet reads store content from the configured store-fragment folder, defaulting to:

`/content/dam/itransparity/stores`

The Store Finder client library is:

`itransparity.storefinder`

The current frontend also uses an IP geolocation lookup and falls back to the browser Geolocation API if the IP lookup is unavailable.

## Authoring Guide

See [AUTHORING_GUIDE.md](AUTHORING_GUIDE.md) for step-by-step authoring instructions and validation scenarios.

## Build

From the project root:

```bash
mvn clean install
```

For local deployment:

```bash
mvn clean install -PautoInstallSinglePackage
```

For a publish deployment profile:

```bash
mvn clean install -PautoInstallSinglePackagePublish
```

## Testing

Unit tests:

```bash
mvn clean test
```

The project also contains integration/UI-test modules. Refer to the module READMEs for module-specific test commands.

## Important Authoring Validation

Before handoff, verify:

1. Header navigation works on desktop and mobile.
2. Hero CTA points to the intended page.
3. Tractor grid displays the expected tractor fragments.
4. Tractor detail displays the correct fragment data and CTA.
5. Store Finder returns the expected dealer data for London, Nairobi, Delhi and Other / unlisted.
6. Manual Store Finder selection updates the dealer list without a full page reload.
7. Use My Location works in a supported browser after location permission is granted.
8. Empty-state text is displayed when no dealers exist.
9. Footer copyright text is correct.

## Repository Structure

- `core` - Sling Models, servlets and other Java/OSGi code.
- `ui.apps` - AEM components, dialogs and client libraries.
- `ui.content` - sample/site content and packaged content.
- `ui.frontend` - frontend build/source where applicable.
- `ui.tests` - UI test resources.
