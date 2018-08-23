package com.coryswainston.smart.dictionary.helpers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the parsing helper
 */
public class ParsingHelperTest {

    private static final String GOOD_RESPONSE = "{\n" +
            "  \"metadata\": {\n" +
            "    \"provider\": \"Oxford University Press\"\n" +
            "  },\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"id\": \"get\",\n" +
            "      \"language\": \"en\",\n" +
            "      \"lexicalEntries\": [\n" +
            "        {\n" +
            "          \"derivatives\": [\n" +
            "            {\n" +
            "              \"id\": \"gettable\",\n" +
            "              \"text\": \"gettable\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"entries\": [\n" +
            "            {\n" +
            "              \"etymologies\": [\n" +
            "                \"Middle English: from Old Norse geta ‘obtain, beget, guess’; related to Old English gietan (in begietan ‘beget’, forgietan ‘forget’), from an Indo-European root shared by Latin praeda ‘booty, prey’, praehendere ‘get hold of, seize’, and Greek khandanein ‘hold, contain, be able’\"\n" +
            "              ],\n" +
            "              \"grammaticalFeatures\": [\n" +
            "                {\n" +
            "                  \"text\": \"Present\",\n" +
            "                  \"type\": \"Tense\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"homographNumber\": \"000\",\n" +
            "              \"notes\": [\n" +
            "                {\n" +
            "                  \"text\": \"The verb get is in the top five of the most common verbs in the English language. Nevertheless, there is still a feeling that almost any use containing get is somewhat informal. No general informal label has been applied to this dictionary entry, but in formal writing it is worth bearing this reservation in mind\",\n" +
            "                  \"type\": \"editorialNote\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"senses\": [\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"come to have (something); receive\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"I got a letter from him the other day\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"text\": \"what kind of reception did you get?\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.014\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"with object\",\n" +
            "                      \"type\": \"grammaticalNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"come to have or hold\"\n" +
            "                  ],\n" +
            "                  \"subsenses\": [\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"experience, suffer, or be afflicted with (something bad)\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I got a sudden pain in my left eye\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.021\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"experience or be afflicted with\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.009\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"receive as a punishment or penalty\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I'll get the sack if things go wrong\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.022\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"receive as punishment or penalty\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"contract (a disease or ailment)\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I might be getting the flu\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.023\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"contract disease etc.\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.008\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.001\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get_something_back\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.034\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.002\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"succeed in attaining, achieving, or experiencing; obtain\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"he got a teaching job in California\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"text\": \"I need all the sleep I can get\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.026\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"with object\",\n" +
            "                      \"type\": \"grammaticalNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"obtain or achieve\"\n" +
            "                  ],\n" +
            "                  \"subsenses\": [\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"move in order to pick up or bring (something); fetch\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I'll get you a drink\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"get another chair\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.027\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"fetch\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.004\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"prepare (a meal)\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"Celia went to the kitchen to start getting their dinner\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.028\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"prepare meal\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.016\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"tend to meet with or find in a specified place or situation\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"for someone used to the tiny creatures we get in England it was something of a shock\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.029\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"with object and adverbial\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"meet with or find in specified place etc.\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"travel by or catch (a bus, train, or other form of transport)\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I got a taxi across to Baker Street\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.030\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"travel by or catch bus, train, etc.\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.007\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"obtain (a figure or answer) as a result of calculation.\"\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.031\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"obtain figure etc. as a result of calculation\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"make contact with, especially by telephone\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"you can get me at home if you need me\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.032\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"make contact with\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.010\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"respond to a ring of (a telephone or doorbell)\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I'll get the door!\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.033\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"respond to ring of telephone etc.\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"used to draw attention to someone whom one regards as pretentious or vain\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"get her!\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.034\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"in imperative\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"registers\": [\n" +
            "                        \"informal\"\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"used to criticize or ridicule someone\"\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.001\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"reach or cause to reach a specified state or condition\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"I need to get my hair cut\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"text\": \"he'd got thinner\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"text\": \"it's getting late\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"text\": \"you'll get used to it\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.036\",\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"reach or cause to reach specified state or condition\"\n" +
            "                  ],\n" +
            "                  \"subsenses\": [\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"used with past participle to form the passive mood\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"the cat got drowned\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.037\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"as auxiliary verb\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"used with participle to form passive mood\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"cause to be treated in a specified way\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"get the form signed by a doctor\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.038\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"with object and past participle\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"cause to be treated in specified way\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.015\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"induce or prevail upon (someone) to do something\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"they got her to sign the consent form\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.039\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"with object and infinitive\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"induce to do\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.014\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"have the opportunity to do\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"he got to try out a few of these nice new cars\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.040\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"no object, with infinitive\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"have the opportunity to do\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.015\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"begin to be or do something, especially gradually or by chance\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"we got talking one evening\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.041\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"no object, with present participle or infinitive\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"begin to be or do something\"\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.003\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"come, go, or make progress eventually or with some difficulty\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"Nigel got home very late\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"text\": \"he hadn't got very far with the book yet\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.043\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"no object, with adverbial of direction\",\n" +
            "                      \"type\": \"grammaticalNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"come, go, or make progress\"\n" +
            "                  ],\n" +
            "                  \"subsenses\": [\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"move or come into a specified position, situation, or state\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"Henry got to his feet\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"she got into the car\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"you don't want to get into debt\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.044\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"no object, with adverbial\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"move or come into specified position or state\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.003\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"succeed in making (someone or something) come, go, or move somewhere\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"she had to get them away from the rocks\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"let's get you home\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.045\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"with object and adverbial\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"cause to come, go, or make progress\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.015\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"reach a specified point or stage\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"it's getting so I can't even think\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.046\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"no object, with clause\",\n" +
            "                          \"type\": \"grammaticalNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"regions\": [\n" +
            "                        \"North American\"\n" +
            "                      ],\n" +
            "                      \"registers\": [\n" +
            "                        \"informal\"\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"reach specified point or stage\"\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.013\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get_back\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.033\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"crossReferenceMarkers\": [\n" +
            "                    \"see have\"\n" +
            "                  ],\n" +
            "                  \"crossReferences\": [\n" +
            "                    {\n" +
            "                      \"id\": \"have\",\n" +
            "                      \"text\": \"have\",\n" +
            "                      \"type\": \"see also\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.049\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"\\\"have got\\\"\",\n" +
            "                      \"type\": \"wordFormNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"have got\"\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"catch or apprehend (someone)\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"the police have got him\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.051\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"with object\",\n" +
            "                      \"type\": \"grammaticalNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"catch or apprehend\"\n" +
            "                  ],\n" +
            "                  \"subsenses\": [\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"strike or wound (someone) with a blow or missile\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"you got me in the eye!\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.052\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"strike or wound with blow etc.\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"punish, injure, or kill (someone), especially as retribution\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I'll get you for this!\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.053\",\n" +
            "                      \"registers\": [\n" +
            "                        \"informal\"\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"punish, injure, or kill\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.017\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"be punished, injured, or killed\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"wait until dad comes home, then you'll get it!\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.054\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"\\\"get it\\\"\",\n" +
            "                          \"type\": \"wordFormNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"registers\": [\n" +
            "                        \"informal\"\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"be punished, injured, or killed\"\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"be appropriately punished or rewarded\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"I'll get mine, you'll get yours, we'll all get wealthy\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.055\",\n" +
            "                      \"notes\": [\n" +
            "                        {\n" +
            "                          \"text\": \"\\\"get mine, his\\\", etc.\",\n" +
            "                          \"type\": \"wordFormNote\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"registers\": [\n" +
            "                        \"informal\"\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"be appropriately punished or rewarded\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.002\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"annoy (someone) greatly\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"cleaning the same things all the time, that's what gets me\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.056\",\n" +
            "                      \"registers\": [\n" +
            "                        \"informal\"\n" +
            "                      ],\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"annoy greatly\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.019\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"definitions\": [\n" +
            "                        \"baffle (someone)\"\n" +
            "                      ],\n" +
            "                      \"examples\": [\n" +
            "                        {\n" +
            "                          \"text\": \"she had got me there: I could not answer\"\n" +
            "                        }\n" +
            "                      ],\n" +
            "                      \"id\": \"m_en_gbus0411550.057\",\n" +
            "                      \"short_definitions\": [\n" +
            "                        \"baffle\"\n" +
            "                      ],\n" +
            "                      \"thesaurusLinks\": [\n" +
            "                        {\n" +
            "                          \"entry_id\": \"get\",\n" +
            "                          \"sense_id\": \"t_en_gb0006315.018\"\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.006\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"understand (an argument or the person making it)\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"What do you mean? I don't get it\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.059\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"with object\",\n" +
            "                      \"type\": \"grammaticalNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"registers\": [\n" +
            "                    \"informal\"\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"understand\"\n" +
            "                  ],\n" +
            "                  \"thesaurusLinks\": [\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.011\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"entry_id\": \"get\",\n" +
            "                      \"sense_id\": \"t_en_gb0006315.012\"\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"acquire (knowledge) by study; learn\"\n" +
            "                  ],\n" +
            "                  \"examples\": [\n" +
            "                    {\n" +
            "                      \"text\": \"that knowledge which is gotten at school\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.061\",\n" +
            "                  \"notes\": [\n" +
            "                    {\n" +
            "                      \"text\": \"with object\",\n" +
            "                      \"type\": \"grammaticalNote\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"registers\": [\n" +
            "                    \"archaic\"\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"learn\"\n" +
            "                  ]\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ],\n" +
            "          \"language\": \"en\",\n" +
            "          \"lexicalCategory\": \"Verb\",\n" +
            "          \"pronunciations\": [\n" +
            "            {\n" +
            "              \"audioFile\": \"http://audio.oxforddictionaries.com/en/mp3/get_gb_1.mp3\",\n" +
            "              \"dialects\": [\n" +
            "                \"British English\"\n" +
            "              ],\n" +
            "              \"phoneticNotation\": \"IPA\",\n" +
            "              \"phoneticSpelling\": \"ɡɛt\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"text\": \"get\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"derivatives\": [\n" +
            "            {\n" +
            "              \"id\": \"gettable\",\n" +
            "              \"text\": \"gettable\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"entries\": [\n" +
            "            {\n" +
            "              \"grammaticalFeatures\": [\n" +
            "                {\n" +
            "                  \"text\": \"Singular\",\n" +
            "                  \"type\": \"Number\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"homographNumber\": \"001\",\n" +
            "              \"notes\": [\n" +
            "                {\n" +
            "                  \"text\": \"The verb get is in the top five of the most common verbs in the English language. Nevertheless, there is still a feeling that almost any use containing get is somewhat informal. No general informal label has been applied to this dictionary entry, but in formal writing it is worth bearing this reservation in mind\",\n" +
            "                  \"type\": \"editorialNote\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"senses\": [\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"an animal's offspring.\"\n" +
            "                  ],\n" +
            "                  \"domains\": [\n" +
            "                    \"Riding\"\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.064\",\n" +
            "                  \"registers\": [\n" +
            "                    \"dated\"\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"animal's offspring\"\n" +
            "                  ]\n" +
            "                },\n" +
            "                {\n" +
            "                  \"definitions\": [\n" +
            "                    \"a person whom the speaker dislikes or despises.\"\n" +
            "                  ],\n" +
            "                  \"id\": \"m_en_gbus0411550.068\",\n" +
            "                  \"regions\": [\n" +
            "                    \"British\"\n" +
            "                  ],\n" +
            "                  \"registers\": [\n" +
            "                    \"dialect\",\n" +
            "                    \"informal\"\n" +
            "                  ],\n" +
            "                  \"short_definitions\": [\n" +
            "                    \"disliked person\"\n" +
            "                  ]\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ],\n" +
            "          \"language\": \"en\",\n" +
            "          \"lexicalCategory\": \"Noun\",\n" +
            "          \"pronunciations\": [\n" +
            "            {\n" +
            "              \"audioFile\": \"http://audio.oxforddictionaries.com/en/mp3/get_gb_1.mp3\",\n" +
            "              \"dialects\": [\n" +
            "                \"British English\"\n" +
            "              ],\n" +
            "              \"phoneticNotation\": \"IPA\",\n" +
            "              \"phoneticSpelling\": \"ɡɛt\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"text\": \"get\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"headword\",\n" +
            "      \"word\": \"get\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void shouldFinishWithoutError() throws Exception {
        ParsingHelper.parseDefinitionsFromJson(GOOD_RESPONSE);
    }

    @Test
    public void parseInflectionsFromResponse() throws Exception {
    }

}