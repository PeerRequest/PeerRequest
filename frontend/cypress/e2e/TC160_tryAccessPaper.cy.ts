/// <reference types="cypress"/>
describe('TC 160: Trying to access the content of a research paper as a non-reviewer', () => {

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
            .get('input[id="conference_year"').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()

            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains('First Conference').click()
            .get('button[aria-label="Submit Paper"]').click()
            .get('input[aria-label="entry_title"]').type("Good Paper")
            .get('input[aria-label="authors"]').type("Donald Duck, Gustav Gans")
            .get('input[aria-label="file_input"]').selectFile("../public/lorem_ipsum.pdf")
            .get('input[aria-label="open_slots"]').type("3")
            .get('button[type="submit"]').click()

            .request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')

    })


    it('TC150_tryAccessPaper', () => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=BBB&given_name=Mortgage' + '&family_name=Backed&email=securities@2008.gb')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
        cy.visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains('First Conference').click()
            .get('a[aria-label="paper_name"]')
            .contains('Good Paper')
            .should('not.have.attr', 'href')
    })

})