package com.coryswainston.smart.dictionary.services;

import com.coryswainston.smart.dictionary.util.DictionaryNetworkUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the DictionaryLookupService
 */
public class DictionaryLookupServiceTest {

    private static final String GOOD_INFLECTION_RESPONSE = "{\n" +
            "  \"metadata\": {},\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"id\": \"word\",\n" +
            "      \"language\": \"language\",\n" +
            "      \"lexicalEntries\": [\n" +
            "        {\n" +
            "          \"grammaticalFeatures\": [\n" +
            "            {\n" +
            "              \"text\": \"string\",\n" +
            "              \"type\": \"string\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"inflectionOf\": [\n" +
            "            {\n" +
            "              \"id\": \"word\",\n" +
            "              \"text\": \"string\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"language\": \"string\",\n" +
            "          \"lexicalCategory\": \"string\",\n" +
            "          \"text\": \"string\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"string\",\n" +
            "      \"word\": \"string\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "\n";

    private static final String GOOD_ENTRY_RESPONSE = "{\n" +
            "  \"metadata\": {},\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"id\": \"string\",\n" +
            "      \"language\": \"string\",\n" +
            "      \"lexicalEntries\": [\n" +
            "        {\n" +
            "          \"derivativeOf\": [\n" +
            "            {\n" +
            "              \"domains\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"id\": \"string\",\n" +
            "              \"language\": \"string\",\n" +
            "              \"regions\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"registers\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"text\": \"string\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"derivatives\": [\n" +
            "            {\n" +
            "              \"domains\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"id\": \"string\",\n" +
            "              \"language\": \"string\",\n" +
            "              \"regions\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"registers\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"text\": \"string\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"entries\": [\n" +
            "            {\n" +
            "              \"etymologies\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"grammaticalFeatures\": [\n" +
            "                {\n" +
            "                  \"text\": \"string\",\n" +
            "                  \"type\": \"string\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"homographNumber\": \"string\",\n" +
            "              \"notes\": [\n" +
            "                {\n" +
            "                  \"id\": \"string\",\n" +
            "                  \"text\": \"string\",\n" +
            "                  \"type\": \"string\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"pronunciations\": [\n" +
            "                {\n" +
            "                  \"audioFile\": \"string\",\n" +
            "                  \"dialects\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"phoneticNotation\": \"string\",\n" +
            "                  \"phoneticSpelling\": \"string\",\n" +
            "                  \"regions\": [\n" +
            "                    \"string\"\n" +
            "                  ]\n" +
            "                }\n" +
            "              ],\n" +
            "              \"senses\": [\n" +
            "                {\n" +
            "                  \"crossReferenceMarkers\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"crossReferences\": [\n" +
            "                    {\n" +
            "                      \"id\": \"string\",\n" +
            "                      \"text\": \"string\",\n" +
            "                      \"type\": \"string\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"definitions\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"domains\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"domains\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"id\": \"string\",\n" +
            "                          \"text\": \"string\",\n" +
            "                          \"type\": \"string\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"regions\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"registers\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"senseIds\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"text\": \"string\",\n" +
            "                      \"translations\": [\n" +
            "                        {\n" +
            "                          \"domains\": [\n" +
            "                            \"string\"\n" +
            "                          ],\n" +
            "                          \"grammaticalFeatures\": [\n" +
            "                            {\n" +
            "                              \"text\": \"string\",\n" +
            "                              \"type\": \"string\"\n" +
            "                            }\n" +
            "                          ],\n" +
            "                          \"language\": \"string\",\n" +
            "                          \"notes\": [\n" +
            "                            {\n" +
            "                              \"id\": \"string\",\n" +
            "                              \"text\": \"string\",\n" +
            "                              \"type\": \"string\"\n" +
            "                            }\n" +
            "                          ],\n" +
            "                          \"regions\": [\n" +
            "                            \"string\"\n" +
            "                          ],\n" +
            "                          \"registers\": [\n" +
            "                            \"string\"\n" +
            "                          ],\n" +
            "                          \"text\": \"string\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"string\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"id\": \"string\",\n" +
            "                      \"text\": \"string\",\n" +
            "                      \"type\": \"string\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"pronunciations\": [\n" +
            "                    {\n" +
            "                      \"audioFile\": \"string\",\n" +
            "                      \"dialects\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"phoneticNotation\": \"string\",\n" +
            "                      \"phoneticSpelling\": \"string\",\n" +
            "                      \"regions\": [\n" +
            "                        \"string\"\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"regions\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"registers\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"subsenses\": [\n" +
            "                    {}\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"string\",\n" +
            "                      \"sense_id\": \"string\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"translations\": [\n" +
            "                    {\n" +
            "                      \"domains\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"grammaticalFeatures\": [\n" +
            "                        {\n" +
            "                          \"text\": \"string\",\n" +
            "                          \"type\": \"string\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"language\": \"string\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"id\": \"string\",\n" +
            "                          \"text\": \"string\",\n" +
            "                          \"type\": \"string\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"regions\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"registers\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"text\": \"string\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"variantForms\": [\n" +
            "                    {\n" +
            "                      \"regions\": [\n" +
            "                        \"string\"\n" +
            "                      ],\n" +
            "                      \"text\": \"string\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                }\n" +
            "              ],\n" +
            "              \"variantForms\": [\n" +
            "                {\n" +
            "                  \"regions\": [\n" +
            "                    \"string\"\n" +
            "                  ],\n" +
            "                  \"text\": \"string\"\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ],\n" +
            "          \"grammaticalFeatures\": [\n" +
            "            {\n" +
            "              \"text\": \"string\",\n" +
            "              \"type\": \"string\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"language\": \"string\",\n" +
            "          \"lexicalCategory\": \"string\",\n" +
            "          \"notes\": [\n" +
            "            {\n" +
            "              \"id\": \"string\",\n" +
            "              \"text\": \"string\",\n" +
            "              \"type\": \"string\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"pronunciations\": [\n" +
            "            {\n" +
            "              \"audioFile\": \"string\",\n" +
            "              \"dialects\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"phoneticNotation\": \"string\",\n" +
            "              \"phoneticSpelling\": \"string\",\n" +
            "              \"regions\": [\n" +
            "                \"string\"\n" +
            "              ]\n" +
            "            }\n" +
            "          ],\n" +
            "          \"text\": \"string\",\n" +
            "          \"variantForms\": [\n" +
            "            {\n" +
            "              \"regions\": [\n" +
            "                \"string\"\n" +
            "              ],\n" +
            "              \"text\": \"string\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"pronunciations\": [\n" +
            "        {\n" +
            "          \"audioFile\": \"string\",\n" +
            "          \"dialects\": [\n" +
            "            \"string\"\n" +
            "          ],\n" +
            "          \"phoneticNotation\": \"string\",\n" +
            "          \"phoneticSpelling\": \"string\",\n" +
            "          \"regions\": [\n" +
            "            \"string\"\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"string\",\n" +
            "      \"word\": \"string\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String INFLECTION_PATH = "https://od-api.oxforddictionaries.com:443/api/v1/inflections/language/word";
    private static final String ENTRY_PATH = "https://od-api.oxforddictionaries.com:443/api/v1/entries/language/word";

    @Mock
    DictionaryNetworkUtils mockNetworkUtils;
    @Mock
    HttpsURLConnection mockInflectionConnection;
    @Mock
    HttpsURLConnection mockEntryConnection;

    DictionaryLookupService testService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockInflectionConnection.getInputStream()).thenReturn(new ByteArrayInputStream(GOOD_INFLECTION_RESPONSE.getBytes()));
        when(mockEntryConnection.getInputStream()).thenReturn(new ByteArrayInputStream(GOOD_ENTRY_RESPONSE.getBytes()));
        when(mockInflectionConnection.getResponseCode()).thenReturn(200);
        when(mockEntryConnection.getResponseCode()).thenReturn(200);

        doNothing().when(mockInflectionConnection).setRequestProperty(anyString(), anyString());
        doNothing().when(mockEntryConnection).setRequestProperty(anyString(), anyString());

        when(mockNetworkUtils.connectToUrl(eq(INFLECTION_PATH))).thenReturn(mockInflectionConnection);
        when(mockNetworkUtils.connectToUrl(eq(ENTRY_PATH))).thenReturn(mockEntryConnection);

        testService = new DictionaryLookupService(mockNetworkUtils)
            .withLanguage("language")
            .withCallback(new DictionaryLookupService.Callback() {
                @Override
                public void callback(String word, String definitions) {
                    // do nothing
                }
            });
    }

    @Test
    public void shouldCompleteWithoutError() throws Exception {
        testService.doInBackground("word");
    }
}