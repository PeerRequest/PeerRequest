const pagination = [
    {name: 1, href: ""},
    {name: 2, href: ""},
    {name: 3, href: ""},
    {name: 4, href: ""},
    {name: 5, href: ""}
];

const categories = [
    {
        id: 1,
        type: "Internal",
        year: 2022,
        name: "International Paper Conference",
        deadline: "Tue, 29. Nov 2022",
        open: true,
        is_my_category: () => true
    },
    {
        id: 2,
        type: "External",
        year: 2022,
        name: "International Paper Conference",
        deadline: "Tue, 29. Nov 2022",
        open: false,
        is_my_category: () => true
    },
    {
        id: 3,
        year: 2023,
        type: "Internal",
        name: "KIT Paper Conference",
        deadline: "Tue, 29. Nov 2022",
        open: false,
        is_my_category: () => true
    },
    {
        id: 4,
        year: 2023,
        type: "External",
        name: "KIT Paper Conference",
        deadline: "Tue, 29. Nov 2022",
        open: false,
        is_my_category: () => true
    },
    {
        id: 5,
        year: 2023,
        type: "Internal",
        name: "German Paper Conference",
        deadline: "Tue, 29. Nov 2022",
        open: false,
        is_my_category: () => false
    },
    {
        id: 6,
        year: 2023,
        type: "External",
        name: "German Paper Conference",
        deadline: "Tue, 29. Nov 2022",
        open: false,
        is_my_category: () => false
    }
];

const papers = [
    {
        id: 1,
        title: "Great Paper #001",
        researcher: "Kaori Chihiro",
        category: categories[0],
        slots: 5
    },
    {
        id: 2,
        title: "Great Paper #002",
        researcher: "Eden Guzman",
        category: categories[0],
        slots: 2
    },
    {
        id: 3,
        title: "Great Paper #003",
        researcher: "Ravi Pearce",
        category: categories[1],
        slots: 1
    },
    {
        id: 4,
        title: "Great Paper #004",
        researcher: "Kaitlyn Fletcher",
        category: categories[3],
        slots: 7
    },
    {
        id: 5,
        title: "Great Paper #005",
        researcher: "Tommy Partridge",
        category: categories[4],
        slots: 0
    }
];

const users = [
    {id: 1, name: "Robert Gouth"},
    {id: 2, name: "Jese Leos"},
    {id: 3, name: "Bonnie Green"},
    {id: 4, name: "Elysia Barnett"},
    {id: 5, name: "Eshal Ali"},
    {id: 6, name: "Duncan Stuart"},
    {id: 7, name: "Marion Calderon"},
    {id: 8, name: "Muhammed Valencia"},
    {id: 9, name: "Niall Dickerson"},
    {id: 10, name: "Hajra Brock"},
    {id: 11, name: "Sabrina Beck"},
    {id: 12, name: "Jake Vazquez"},
    {id: 13, name: "Karol Bender"},
    {id: 14, name: "Cassie Suarez"},
    {id: 15, name: "Felicity Baird"},
    {id: 16, name: "Aneesa Key"},
    {id: 17, name: "Stephen Orr"},
    {id: 18, name: "Erik Carroll"},
    {id: 19, name: "Carrie Davenport"},
    {id: 20, name: "Keiran Aguilar"},
    {id: 21, name: "Zach Phelps"},
    {id: 22, name: "Myla Navarro"},
    {id: 23, name: "Roosevelt Booth"},
    {id: 24, name: "Kaori Chihiro"}
];

const reviews = [
    {
        id: 1,
        reviewer: "Kaori Chihiro",
        paper: papers[1]
    }
]

const comments = [
    {
        id: 0,
        user: "Mysterious other user",
        content: "Lorem ipsum dolor sit amet, adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        date: "Mon Apr 12 2021 14:41:24 GMT-0400 (Horário Padrão do Amazonas)"
    }
]

export default {
    pagination,
    categories,
    papers,
    users,
    reviews,
    comments
};