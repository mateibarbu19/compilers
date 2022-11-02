(*******************************
 *** Classes Product-related ***
 *******************************)
class Product {
    name : String;
    model : String;
    price : Int;

    init(n : String, m : String, p : Int) : SELF_TYPE {{
        name <- n;
        model <- m;
        price <- p;
        self;
    }};

    getprice() : Int { price * 119 / 100 };

    to_string() : String {
        type_name().concat("(").concat(name).concat(",").concat(model).concat(")")
    };
};

class Edible inherits Product {
    -- VAT tax is lower for foods
    getprice() : Int { price * 109 / 100 };
};

class Soda inherits Edible {
    -- sugar tax is 20 bani
    getprice() : Int { price * 109 / 100 + 20 };
};

class Coffee inherits Edible {
    -- this is technically poison for ants
    getprice() : Int { price * 119 / 100 };
};

class Laptop inherits Product {
    -- operating system cost included
    getprice() : Int { price * 119 / 100 + 499 };
};

class Router inherits Product {};

(****************************
 *** Classes Rank-related ***
 ****************************)
class Rank {
    name : String;

    init(n : String) : SELF_TYPE {{
        name <- n;

        self;
    }};

    to_string() : String {
        -- Hint: what are the default methods of Object?
        type_name().concat("(").concat(name).concat(")")
    };
};

class Private inherits Rank {};

class Corporal inherits Private {};

class Sergent inherits Corporal {};

class Officer inherits Sergent {};

class Helper {
    describes_product(s : String) : Bool {
        if
            s = new Soda.type_name()
        then
            true
        else
            if
                s = new Coffee.type_name()
            then
                true
            else
                if
                    s = new Laptop.type_name()
                then
                    true
                else
                    if
                        s = new Router.type_name()
                    then
                        true
                    else
                        false
                    fi
                fi
            fi
        fi
    };

    describes_rank(s : String) : Bool {
        if
            s = new Private.type_name()
        then
            true
        else
            if
                s = new Corporal.type_name()
            then
                true
            else
                if
                    s = new Sergent.type_name()
                then
                    true
                else
                    if
                        s = new Officer.type_name()
                    then
                        true
                    else
                        false
                    fi
                fi
            fi
        fi
    };

    build_product(type : String, name : String, model : String, price : Int) : Product {
        if
            type = new Soda.type_name()
        then
            new Soda.init(name, model, price)
        else
            if
                type = new Coffee.type_name()
            then
                new Coffee.init(name, model, price)
            else
                if
                    type = new Laptop.type_name()
                then
                    new Laptop.init(name, model, price)
                else
                    if
                        type = new Router.type_name()
                    then
                        new Router.init(name, model, price)
                    else
                        new Product
                    fi
                fi
            fi
        fi
    };

    build_rank(type: String, name: String) : Rank {
        if
            type = new Private.type_name()
        then
            new Private.init(name)
        else
            if
                type = new Corporal.type_name()
            then
                new Corporal.init(name)
            else
                if
                    type = new Sergent.type_name()
                then
                    new Sergent.init(name)
                else
                    if
                        type = new Officer.type_name()
                    then
                        new Officer.init(name)
                    else
                        new Rank
                    fi
                fi
            fi
        fi
    };
};
