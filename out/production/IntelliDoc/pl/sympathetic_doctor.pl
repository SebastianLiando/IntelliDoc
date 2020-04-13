/* depends on knowledge base and utilities script. Remove the comment below to use in prolog console. The java codes consults each script */
%:- ['kb.pl', 'utils.pl'].

/**
 * Construct the question to ask about the patient's pain.
 *
 * @param Pain the pain level to be asked
 * @param Gesture the gesture to act
 * @param Question the question to ask
 */
ask_pain(Pain, Gesture, Question) :- fetch_pains(Pain), assert(asked(Pain)), 							% get a pain, mark as asked
							fetch_gesture(none, none, Gesture, _), fetch_prefix(intro_pain, Prefix),	% get gesture and prefix
							english_of(Pain, Pain_en), construct_question(Prefix, Pain_en, Question);   % construct into an English sentence
							ask_pain(Pain, Gesture, Question).											% repeat if not answered

/* mark all pain as asked */
assert_all_pain(_) :- pain_library(PainList), member(Pain, PainList), 									% get a pain from library
				not(asked(Pain)), assert(asked(Pain)).				  									% if not marked, mark it


/**
 * Construct the question to ask about the patient's mood.
 *
 * @param Mood the mood to be asked
 * @param Gesture the gesture to act
 * @param Question the question to ask
 */
ask_mood(Mood, Gesture, Question) :- fetch_moods(Mood), assert(asked(Mood)), 							% get a mood, mark as asked
							fetch_gesture(none, none, Gesture, _), fetch_prefix(intro_mood, Prefix),	% get gesture and prefix
							english_of(Mood, Mood_en), construct_question(Prefix, Mood_en, Question); 	% construct into an English sentence
							ask_mood(Mood, Gesture, Question).											% repeat if not answered

/**
 * Construct the question to ask about the patient's symptom of a chosen disease. 
 * Requires mood and pain to be asked first.
 *
 * @param Disease the disease examined
 * @param Symptom the symptom to be asked
 * @param Gesture the gesture to act
 * @param Question the question to ask
 */
ask_symptom(Disease, Symptom, Gesture, Question) :- patient_mood(Mood), patient_pain(Pain), 			% get patient's mood and pain
							fetch_symptoms(Disease, Symptom), english_of(Symptom, Symptom_en), 			% get a symptom to ask, translate it into English
							fetch_gesture(Mood, Pain, Gesture, Type), fetch_prefix(Type, Prefix), 		% get gesture and prefix	
							construct_question(Prefix, Symptom_en, Question).							% construct into an English sentence

/**
 * Construct an English question by joining prefix, the content, and a question mark.
 *
 * @param Prefix the prefix
 * @param Content the content
 * @param Result the English question
 */							
construct_question(Prefix, Content, Result) :- string_concat(Prefix, Content, Tmp), string_concat(Tmp, "?", Result).

/** Get the prefix to say when the diagnosis is finished.
 *
 * @param Gesture the gesture 
 * @param Prefix the prefix
 * @param Context the context of diagnosis (perfect match, partial match, no match)
 */
outro(Gesture, Prefix, Context) :- patient_mood(Mood), patient_pain(Pain), 								% get patient's mood and pain
							fetch_gesture(Mood, Pain, Gesture, _), fetch_prefix(Context, Prefix).		% get gesture and prefix	

/* Get a list of pains in random order one at a time */
fetch_pains(Pain) :- pain_library(PainList), random_permutation(PainList, PainOrder), member(Pain, PainOrder).  

/* Get a list of moods in random order one at a time */				
fetch_moods(Mood) :- mood_library(MoodList), random_permutation(MoodList, MoodOrder), member(Mood, MoodOrder).  				

/* Get a list of diseases to examine in random order one at a time*/
fetch_diseases(Disease) :- disease_library(DiseaseList), random_permutation(DiseaseList, DiseaseOrder), member(Disease, DiseaseOrder).

/* Get a list of symptoms of the disease in random order one at a time. Only those symptoms which are not asked yet.*/
fetch_symptoms(Disease, Symptom):- disease(Disease, SymptomList), random_permutation(SymptomList, SymptomOrder), % get the random order
							member(Tmp, SymptomOrder), 															 % get a symptom
							not_a_repeat(Tmp), Symptom = Tmp, assert(asked(Symptom)).							 % if not asked, mark as asked, and ask the patient 

/* check if X has been asked */
not_a_repeat(X) :- (asked(X); patient_mood(X); patient_pain(X)) -> false; true.

/* get a random gesture for the mood and pain from the gesture library */
fetch_gesture(Mood, Pain, Gesture, Type):- use_gesture(Mood, Pain, Type), gesture(Type,GestureList), 
							  random_member(Gesture, GestureList).

/* get a random prefix based on the context from the prefix library */							 
fetch_prefix(Context, Prefix) :- prefix(Context, PrefixList), random_member(Prefix, PrefixList).

/* check if the disease is valid based on previous queries to the patient */
valid_disease(Disease) :- disease(Disease, SymptomList), valid_symptom(SymptomList).

/* check if the symptom is valid (has not been asked or has been asked and is the patient's symptom)*/
valid_symptom([]) :- true.																				%base case
valid_symptom([H|T]) :- (asked(H) -> ((patient_mood(H); patient_pain(H); patient_symptom(H)) -> true; false); true), 
						valid_symptom(T).

/* check if patient has any disease with perfect match and bind it to X */
has_disease(X):- disease_library(Z), member(X, Z), has_symptoms_for_disease(X).

/* true if the patient has the exact symptoms as the disease */
has_symptoms_for_disease(Disease) :- disease(Disease, DiseaseList), length(DiseaseList, ListLength),	% get number of symptoms
								count_match(DiseaseList, MatchLength), ListLength = MatchLength.		% number of match = number of symptoms

/* get the potential diseases one by one */ 
fetch_estimate(Entry) :- build_estimate(ResultList), member(Entry, ResultList).

/* build lists of potential diseases and sort in descending order based on number of symptoms matched */
build_estimate(ResultList) :- disease_library(DiseaseList), build_tuple(DiseaseList, UnsortedList),		% build the list
							sort(2, @>=, UnsortedList, ResultList).										% sort based on count

/* builds a collection of potential disease and its symptom match count */
build_tuple([],[]).																						% base case 
build_tuple([H|T], ResultList) :- build_tuple(T, Tmp), count_symptoms(H, Count), 						% traverse to the end, and count the match
								(not(Count = 0) -> append(Tmp, [[H,Count]],ResultList);					% if match is not 0, append to result
								append(Tmp, [], ResultList)).

/** Count the number of symptoms matched for a disease.
 *
 * @param Disease the disease 
 * @param Res the count
 */							
count_symptoms(Disease, Res) :- disease(Disease, SymptomList), count_match(SymptomList, Res). 

/* match counter */
count_match([], 0).
count_match([H|T], Res) :- count_match(T, Tmp), 															% traverse to the end
					((patient_mood(H); patient_pain(H); patient_symptom(H)) -> Res is Tmp+1 ; Res is Tmp).  % if match, increment the result


