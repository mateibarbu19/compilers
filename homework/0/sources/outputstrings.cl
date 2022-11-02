class OutputStrings {
    help_banner() : String {
        "This is a library which implements a generic list, with items of any "
        .concat("type. Supperted operations:\n\n")

        .concat("> load: Reads items, one per line, until 'END' is met.\n")

        .concat("> print [index]: Print either all lists or that at the given ")
        .concat("index.\n")

        .concat("> merge <index1> <index2>: Merge two lists, removes them, and")
        .concat(" adds the result at the end.\n")

        -- .concat("sortBy <index> <comparator> <mode>: Sorts the list at the ")
        -- .concat("given index according to the given comparator and in the ")
        -- .concat("way given by the mode: ascendent or descendent. The ")
        -- .concat("available comparators are:\n")
        -- .concat("\tPriceComparator: compares items by their price.\n")
        -- .concat("\tRankComparator: compares ranks by their price.\n")
        -- .concat("\tAlphabeticComparator: compares 'String' items ")
        -- .concat("alphabetically.\n")
        -- .concat("filterBy <index> <type>: Applies the given filter type to "
        -- .concat("the list at the given filter. The supported filter types ")
        -- .concat("are:\n")
        -- .concat("\tProductFilter: Filters out non-product items.\n")
        -- .concat("\tRankFilter: Filters out non-rank items.\n")
        -- .concat("\tSamePriceFilter: Keeps the products whose price is the "
        -- .concat("same as if it were a genric product.\n")
    };

    list_delimiter() : String { ": " };
};
