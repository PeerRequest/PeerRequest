/// <reference types="cypress"/>
describe('Entries', () => {

    before(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max' + '&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })

            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('button[aria-label="Create Category"]').click()
            .get('input[id="conference_name"]').type("First Conference")
            .get('input[id="conference_year"]').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()

        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })

    beforeEach(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max' + '&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .wait(500)
            .get('a[aria-label="category_name"]')
            .contains("First Conference").click()
    })

    afterEach(() => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })


    it('TC100: Create an entry', () => {
        cy
            .get('button[aria-label="Submit Paper"]').click()
            .get('input[aria-label="entry_title"]').type("Good Paper")
            .get('input[aria-label="authors"]').type("Sarah Smith")
            .get('input[aria-label="file_input"]').selectFile("../public/lorem_ipsum.pdf")
            .get('input[aria-label="open_slots"]').type("3")
            .get('button[type="submit"]').click()
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper")
            .should('be.visible')
        cy
            .get('button[aria-label="Submit Paper"]').click()
            .get('input[aria-label="entry_title"]').type("Delete Paper")
            .get('input[aria-label="authors"]').type("Sarah Smith")
            .get('input[aria-label="file_input"]').selectFile("../public/lorem_ipsum.pdf")
            .get('input[aria-label="open_slots"]').type("3")
            .get('button[type="submit"]').click()
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper")
            .should('be.visible')
    })

    it('TC120: Edit an entry', () => {
        cy
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper").click()
            .get('button[aria-label="edit_paper"]').click()
            .get('input[aria-label="edit_paper_authors"]').type("{selectall}{backspace}Kaori Chihiro")
            .get('button[type="submit"]').click()
            .wait(2000)
            .get('div[aria-label="paper_authors"]')
            .contains('Authors: Kaori Chihiro')
            .should('be.visible')
    });

    it('TC140: Delete an entry', () => {
        cy
            .get('a[aria-label="paper_name"]')
            .contains("Delete Paper").click()
            .get('button[aria-label="delete_paper"]').click()
            .get('button[aria-label="Confirm deletion"]').click()
            .get('a[aria-label="paper_name"]')
            .contains('Delete Paper')
            .should('not.exist')
    })

    it('TC150: Download a research paper', () => {
        cy
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper").click()
            .get('span[aria-label="download_paper"]').get('a').should('have.attr', 'href')
    });

    it('TC160: Try to access a paper as a user without permission', () => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
            .request('GET', 'http://localhost:8080/test/auth/login?user_id=1&user_name=test1&given_name=Helma&family_name=Gunter&email=helma@gunter.de')
            .then((resp) => {
                const cookieString = resp.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
        cy.wait(1000)
        cy.visit('http://localhost:8080')
            .wait(1000)
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains('First Conference').click()
            .get('[aria-label="paper_name"]')
            .contains('Good Paper')
            .should('not.have.attr', 'href')
    })

})