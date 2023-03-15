describe('Categories', () => {

    before(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max' +
            '&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.log(JSON.stringify(response)).setCookie('JSESSIONID',
                    cookieString.substring(11, cookieString.length - 18))
            }).visit('http://localhost:8080')
    })

    it('login', () => {
        cy.get('a[href="/categories"]').click()
        .get('button[id="avatar_with_name"]').click()
    })
})

