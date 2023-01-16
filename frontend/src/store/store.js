import {persistStore} from './persistStore.js'

const defaultData = [
    {
        "user": {
            "name": "Kaori Chihiro",
        },
        "comments": [
            {
                id: 0,
                user: "Mysterious other user",
                content: "Lorem ipsum dolor sit amet, adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                date: "Mon Apr 12 2021 14:41:24 GMT-0400 (Horário Padrão do Amazonas)"
            }
        ]
    }
]

export const store = persistStore('data', defaultData)
