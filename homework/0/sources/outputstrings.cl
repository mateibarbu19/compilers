class OutputStrings {
    help_banner() : String {
        "This is a library which implements a generic list, with items of any "
        .concat("type. Supperted operations:\n\n")

        .concat("> load: Reads items, one per line, until 'END' is met.\n")

        .concat("> print [index]: Print either all lists or that at the given ")
        .concat("index.\n")

        .concat("> merge <index1> <index2>: Merge two lists, removes them, and")
        .concat(" adds the result at the end.\n")

        .concat("> filterBy <index> <type>: Applies a filter to the list at a given ")
        .concat("index.\n")

        .concat("> sortBy <index> <comparator> <mode>: Sorts the list at the ")
        .concat("given index with a comparator in a ascending mode or not.")
    };

    list_delimiter() : String { ": " };
};
