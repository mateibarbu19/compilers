class Main inherits IO {
    lists : List;
    looping : Bool <- true;
    working_list : List <- new List;

    main() : Object {{
        let
            word : String,
            in_strs: InputStrings <- new InputStrings,
            out_strs: OutputStrings <- new OutputStrings
        in while looping loop {
            word <- in_string();

            if word = in_strs.cmd_help() then
                out_string(out_strs.help_banner())
            else if word = in_strs.cmd_print() then
                out_string(out_strs.help_banner())
            else if word = in_strs.cmd_load() then
                abort()
                -- face o noua lista
                -- stocata la finalul lui lists
            else
                out_string(word.concat("\n"))
            fi fi fi;
        } pool;

        self;
    }};
};
