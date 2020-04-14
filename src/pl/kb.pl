/* pain library */
pain_library([unbearable_pain, lot_of_pain, manageable_pain, mild_pain, no_pain]).

/* serious pains library  */
serious_pain_library([unbearable_pain, lot_of_pain]).

/* mood library */
mood_library(["calm", "angry", "weepy", "stressed", "exhausted"]).

/* gesture library */
gesture(polite_gesture,["Look Concerned", "Mellow Voice", "Light Touch", "Faint Smile", "Look in the Eye"]).
gesture(sympathetic_gesture, ["Faint Smile", "UwU", "Tilt Head A Bit", "Speak Smoothly"]).
gesture(calming_gesture,["Greet", "Look Composed", "Look Attentive", "Smiles"]).
gesture(normal_gesture, ["Broad Smile", "Joke", "Beaming Voice", "Confident Smile", "Folds Arm"]).
gesture(serious_gesture, ["Serious Face", "Raise Eyeglasses", "Nods", "Fold Fingers", "Write Notes"]).

/* prefix library */
prefix(intro_pain, ["Is your pain ", "The pain you have, is it ", "What's your pain? Is it "]).
prefix(intro_mood, ["Are you feeling ", "Is your mood ", "How about your mood, is it "]).

prefix(outro_perfect, ["I know, you suffer from ", "Oh, this must be ", "I'm sure you have "]).
prefix(outro_partial, ["I think you are quiet healthy, here are some potentials",
					"Take a look at the following, you may or may not have these",
					"Maybe one of these is your sickness",
					"Just be wary of these diseases"]).
prefix(outro_none,["Congrats! You are healthy", "Come again when you are sick", "No worries, you are just overthinking it"]).

prefix(serious_gesture, ["Do you suffer from ",
						"And I believe you have ",
						"Usually it's accompanied with ",
						"Heard you clear. How about "]).

prefix(polite_gesture, ["Alright, maybe ", 
						"Understood. How about ",
						"Yes, do you feel "]).

prefix(sympathetic_gesture, ["Ah, sorry, but do you have ",
							"Don't worry, how about ",
							"It's becoming clear, you have ",
							"I will make this quick, do you feel "]).

prefix(calming_gesture, ["That's ok, maybe also ", 
						 "No worries, it's not that bad. Do you also have ",
						 "Understood, do you feel ",
						 "That's fine, how about "]).
						 
prefix(normal_gesture, ["Do you suffer from ", 
						"OK. ",
						"I guess you feel ",
						"Do you feel "]).
						
/**
 * Maps the patient's mood and pain level. If the pain level is considered serious, always use the serious gesture.
 *
 * @param Mood the mood
 * @Param Pain the pain level
 * @Param Type the type of gesture
 */
use_gesture(Mood, Pain, Type) :- serious_pain_library(SeriousPainList),  
							(member_of(Pain, SeriousPainList) -> Type = serious_gesture; match_gesture(Mood, Type)).

/* a mood should match to the gesture type */							
match_gesture(calm, normal_gesture).
match_gesture(angry , polite_gesture).
match_gesture(weepy, sympathetic_gesture).
match_gesture(stressed, calming_gesture).
match_gesture(exhausted, polite_gesture).
match_gesture(none, normal_gesture).		

/* disease list library */
disease_library([fever, cold, injury, asthma, food_poisoning, conjunctivitis, diarrhea, migraine, headache, 
				 insomnia, coronavirus, myopia, hypermetropia, dehydration, vertigo]).

/* disease and its symptoms library*/
disease(fever, 			[high_temperature, intense_sweat, ache, weepy, manageable_pain]).
disease(cold, 			[sneeze, cough, exhausted, high_temperature, mild_pain]).
disease(injury, 		[bleeding, lot_of_pain, weepy, angry]).
disease(asthma, 		[cough, exhausted, breath_shortness, tight_chess, mild_pain]).
disease(food_poisoning, [high_temperature, angry, unbearable_pain, weepy, vomit]).
disease(conjunctivitis, [weepy, unbearable_pain, crusting_eyelids, blurry_vision, itchy_eyes]).
disease(diarrhea, 		[stressed, lot_of_pain, stomachache, watery_stools, bleed]).
disease(migraine, 		[exhausted, manageable_pain, nausea, vomit, blurry_vision]).
disease(headache, 		[stressed, manageable_pain, dizzy, exhausted, ache]).
disease(myopia, 		[angry, mild_pain, blurry_vision, short_sighted, dizziness]).
disease(hypermetropia,  [angry, mild_pain, blurry_vision, long_sighted, dizziness]).
disease(insomnia, 		[stressed, mild_pain, sleep_difficulty, increased_errors, abnormal_sleep]).
disease(coronavirus, 	[exhausted, lot_of_pain, breath_shortness, high_temperature, cough, sneeze]).
disease(muscle_cramp, 	[weepy, unbearable_pain, swelling, ache, walking_difficulty]).
disease(dehydration, 	[exhausted, manageable_pain, thirsty, dizziness, dry_mouth]).
disease(vertigo, 		[unbearable_pain, weepy, vomit, nausea, intense_sweat]).


/* initialize user data*/
:- dynamic patient_mood/1. 			%The patient's mood
:- dynamic patient_pain/1. 			%The patient's pain level
:- dynamic patient_symptom/1. 		%The patient's symptom
:- dynamic asked/1.					%History of asked questions

