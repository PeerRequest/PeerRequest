<script>
    import Container from "../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../components/ResponsiveBreadCrumb.svelte";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import mock_data from "../../../../mock_data.js";
    import Reviews from "../../../../components/Reviews.svelte";
    import Review from "../../../../components/Review.svelte";

    const categories = mock_data.categories;
    const papers = mock_data.papers;
    const reviews = mock_data.reviews;
    const users = mock_data.users;

    export let ongoing = false;

    /** @type {import("./$types").PageData} */
    export let data;
</script>


<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
        <BreadcrumbItem
                href="/categories/{categories[data.category_id - 1].id}">{categories[data.category_id - 1].name}
        </BreadcrumbItem>
        <BreadcrumbItem>Bidding</BreadcrumbItem>
    </ResponsiveBreadCrumb>

    <Heading class="mb-5" tag="h2">{"Assignment for " + categories[data.category_id - 1].name}</Heading>

    <Reviews
            show_reviewer=true
            show_paper=true
    >
        {#each papers.filter((p) => p.category === categories[data.category_id - 1]) as p}
            {#each reviews.filter((r) => r.paper === p) as r}
                <Review
                        href="/categories/{r.paper.category.id}/{r.paper.id}/{r.id}"
                        id={r.id}
                        paper={r.paper}
                        reviewer={users.find(user => user.id === r.reviewer_id).name}
                />
            {/each}
        {/each}
    </Reviews>

</Container>
