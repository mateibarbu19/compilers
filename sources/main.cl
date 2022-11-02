class Main inherits IO {
    lists : List <- new List;
    working_list : List;
    strtok : StrTok <- new StrTok;
    loading : Bool <- true;
    in_strs : InputStrings <- new InputStrings;
    atoi : A2I <- new A2I;

    which() : Object {
        let
            token : String <- strtok.get()
        in
            if
                token = new IO.type_name()
            then
                new Object
            else
                if
                    token = new IO.type_name()
                then
                    new IO
                else
                    if 
                        token = new Int.type_name()
                    then
                        atoi.a2i(strtok.get())
                    else
                        if
                            token = new Bool.type_name()
                        then
                            if 
                                strtok.get() = in_strs.true_str()
                            then
                                true
                            else
                                false
                            fi
                        else
                            if
                                token = new String.type_name()
                            then
                                strtok.get()
                            else
                                true
                            fi
                        fi
                    fi
                fi
            fi
    };

    load() : Object {
        let
            line : String
        in 
            while
                loading
            loop {
                working_list <- new List;
                line <- in_string();

                if 
                    line = in_strs.end_str()
                then {
                    loading <- false;
                    lists.add(working_list);
                    working_list <- new List;
                } else {
                    strtok.init(line, in_strs.delimiter());

                    working_list.add(which());
                }
                fi;
            } pool
    };

    main() : Object {{
        let
            line : String,
            looping : Bool <- true
        in
            while
                looping 
            loop
                if
                    loading
                then
                    load()
                else
                    true
                fi
            pool
        ;

        self;
    }};
};

--     if word = in_strs.cmd_help() then
--         out_string(out_strs.help_banner())
--     else if word = in_strs.cmd_print() then
--         out_string(out_strs.help_banner())
--     else if word = in_strs.cmd_load() then
--         abort()
--         -- face o noua lista
--         -- stocata la finalul lui lists
--     else
--         out_string(word.concat("\n"))
--     fi fi fi;
-- } pool;
