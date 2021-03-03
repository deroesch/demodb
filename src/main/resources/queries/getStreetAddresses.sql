SELECT 
    Person.last_name as "Last Name", 
    Person.first_name as "First Name",
    p2a.label as "Description", 
    Address.addr1 as "Street Location",
    Address.addr2 as "Addr Line 2", 
    Address.city as "City", 
    State.abbrev as "State", 
    Address.zip_code as "Zip Code",
    Address.w3w as "What3Words.com"
FROM 
    public.person Person 
LEFT OUTER JOIN 
    public.person2address p2a 
ON 
    ( 
        Person.id = p2a.person_id) 
LEFT OUTER JOIN 
    public.street_address Address 
ON 
    ( 
        p2a.street_address_id = Address.id) 
LEFT OUTER JOIN 
    public.state_codes State 
ON 
    ( 
        Address.state_code = State.id) 
ORDER BY 
    Person.last_name ASC, 
    Person.first_name ASC, 
    Person.middle_name ASC;