class StrTok {
	string : String;
	last_position : Int;
	delimiter : String;

	init(s: String, d: String) : SELF_TYPE {{
		string <- s;
		delimiter <- d.substr(0, 1);
		last_position <- 0;
		self;
	}};

	get() : String {
		let
			token : String <- "",
			pos : Int <- last_position
		in {
			while
				pos < string.length()
			loop {
				last_position <- last_position + 1;

				pos <-
					if string.substr(pos, 1) = delimiter then
						string.length()
					else {
						token <- token.concat(string.substr(pos, 1));
						last_position;
					} fi;
			} pool;

			token;
		}
	};
};