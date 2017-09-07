/**
 * The packages contained in this module allow the usage of all parts of the IIIF specification.
 * The classes are named after the corresponding IIIF entities.
 *
 * To ensure that the resulting JSON is compliant with the IIIF specification, it is neccessary
 * to use the contained Jackson object mapper ({@link de.digitalcollections.iiif.model.jackson.IiifObjectMapper}),
 * merely using the {@link de.digitalcollections.iiif.model.jackson.IiifModule} is not sufficient,
 * since a lot of custom code was neccessary to achieve compliance, more than what is available
 * with Jackson modules. If you want to override or extend the settings, just subclass the ObjectMapper.
 */
package de.digitalcollections.iiif.model;