package com.coryswainston.smart.dictionary.schema

/**
 * For JSON marshalling
 */
data class Register(var id: String, var text: String)

data class Domain(var id: String, var text: String)

data class Inflection(var id: String, var text: String)

data class LexicalCategory(var id: String, var text: String)

data class GrammaticalFeature(var type: String, var text: String)

data class LemmatronLexicalEntry(var grammaticalFeatures: MutableList<GrammaticalFeature>,
                                 var inflectionOf: MutableList<Inflection>,
                                 var language: String,
                                 var lexicalCategory: LexicalCategory,
                                 var text: String)

data class HeadwordLemmatron(var id: String,
                             var language: String,
                             var lexicalEntries: MutableList<LemmatronLexicalEntry>,
                             var type: String,
                             var word: String)

data class Lemmatron(var metadata: Any, var results: MutableList<HeadwordLemmatron>)

data class VariantForm(var regions: MutableList<String>, var text: String)

data class ThesaurusLink(var entryId: String, var senseId: String)

data class RetrieveEntry(var metadata: Any, var results: MutableList<HeadwordEntry>)

data class RelatedEntry(var domains: MutableList<String>,
                        var id: String,
                        var language: String,
                        var regions: MutableList<String>,
                        var registers: MutableList<String>,
                        var text: String)

data class CategorizedText(var id: String, var text: String, var type: String)

data class CrossReference(var id: String, var text: String, var type: String)

data class Entry(var etymologies: MutableList<String>,
                 var grammaticalFeatures: MutableList<GrammaticalFeature>,
                 var homographNumber: String,
                 var notes: MutableList<CategorizedText>,
                 var pronunciations: MutableList<Pronunciation>,
                 var senses: MutableList<Sense>,
                 var variantForms: MutableList<VariantForm>)

data class Pronunciation(var audioFile: String,
                         var dialects: MutableList<String>,
                         var phoneticNotation: String,
                         var phoneticSpelling: String,
                         var regions: MutableList<String>)

data class Sense(var crossReferenceMarkers: MutableList<String>,
                 var crossReferences: MutableList<CrossReference>,
                 var definitions: MutableList<String>,
                 var domains: MutableList<Domain>,
                 var examples: MutableList<Example>,
                 var id: String,
                 var notes: MutableList<CategorizedText>,
                 var pronunciations: MutableList<Pronunciation>,
                 var regions: MutableList<String>,
                 var registers: MutableList<Register>,
                 var shortDefinitions: MutableList<String>,
                 var subsenses: MutableList<Sense>,
                 var thesaurusLinks: MutableList<ThesaurusLink>,
                 var translations: MutableList<Translation>,
                 var variantForms: MutableList<VariantForm>)

data class Translation(var domains: MutableList<String>,
                       var grammaticalFeatures: MutableList<GrammaticalFeature>,
                       var language: String,
                       var notes: MutableList<CategorizedText>,
                       var regions: MutableList<String>,
                       var registers: MutableList<String>,
                       var text: String)

data class Example(var definitions: MutableList<String>,
                   var domains: MutableList<String>,
                   var notes: MutableList<CategorizedText>,
                   var regions: MutableList<String>,
                   var registers: MutableList<String>,
                   var senseIds: MutableList<String>,
                   var text: String,
                   var translations: MutableList<Translation>)

data class HeadwordEntry(var id: String,
                           var language: String,
                           var lexicalEntries: MutableList<LexicalEntry>,
                           var pronunciations: MutableList<Pronunciation>,
                           var type: String,
                           var word: String)

data class LexicalEntry(var derivativeOf: MutableList<RelatedEntry>,
                        var derivatives: MutableList<RelatedEntry>,
                        var entries: MutableList<Entry>,
                        var grammaticalFeatures: MutableList<GrammaticalFeature>,
                        var language: String,
                        var lexicalCategory: LexicalCategory,
                        var notes: MutableList<CategorizedText>,
                        var pronunciations: MutableList<Pronunciation>,
                        var text: String,
                        var variantForms: MutableList<VariantForm>)
