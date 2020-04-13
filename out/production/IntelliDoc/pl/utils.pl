/**
 * Fetch members of a list one by one.
 *
 *@param X the chosen member
 */
member(X,[X|_]).				 %base case
member(X,[_|R]) :- member(X,R).

/**
 * Append a member to list
 *
 * @param [A|B] the list to append
 * @param C the new member
 * @param [A|D] the result list
 */
append([A | B], C, [A | D]) :- append(B, C, D).
append([], A, A).				%base case

/* Translate non-string prolog value into a string.
 *
 * @param In the non-string prolog value
 * @param Result the string output
 */
english_of(In, Result) :- split_string(In, "_", "", WordList), join(WordList, Result).

/* Convert list of words into a string.
 * For example, "lot_of_pain" will result to "lot of pain"
 * @param [H|T] the list of words
 * @param Result the string output
 */
join([], "").					%base case
join([H|T], Result) :- join(T, Tmp), 															% traverse to the end
				(Tmp = "" -> string_concat(H, Tmp, Result); 									% append last word without space 
							(string_concat(H, " ", Word), string_concat(Word, Tmp, Result))).	% append with space

/* Check if an item is a member of the list. 
 *
 * @param Key the searched item
 * @param [H|T] the list to be checked
 */
member_of(_, []) :- false.
member_of(Key, [H|T]) :- Key = H -> true; member_of(Key, T).