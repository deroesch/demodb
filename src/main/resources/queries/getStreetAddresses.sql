SELECT
    Person.last_name,
    Person.first_name,
    Person.middle_name,
    p2a.label,
    Address.addr1,
    Address.addr2,
    Address.city,
    Address.state,
    Address.zip_code
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
ORDER BY
    Person.last_name ASC,
    Person.first_name ASC,
    Person.middle_name ASC;