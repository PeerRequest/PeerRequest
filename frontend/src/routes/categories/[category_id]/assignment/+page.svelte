<script>
    import Container from "../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../components/ResponsiveBreadCrumb.svelte";
    import {BreadcrumbItem, Button, Heading} from "flowbite-svelte";
    import mock_data from "../../../../mock_data.js";
    import Reviews from "../../../../components/Reviews.svelte";
    import Review from "../../../../components/Review.svelte";

    const categories = mock_data.categories;
    const papers = mock_data.papers;
    const reviews = mock_data.reviews;
    const users = mock_data.users;

    export let ongoing = true;

    /** @type {import("./$types").PageData} */

    export let data;

    function saveAssignment() {
        ongoing = false;

    }
</script>


<Container>
    <div class="flex max-md:flex-wrap justify-between items-center mb-4">
        <div>
            <ResponsiveBreadCrumb>
                <BreadcrumbItem home href="/">Home</BreadcrumbItem>
                <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
                <BreadcrumbItem
                        href="/categories/{categories[data.category_id - 1].id}">{categories[data.category_id - 1].name}
                </BreadcrumbItem>
                <BreadcrumbItem>Assignment</BreadcrumbItem>
            </ResponsiveBreadCrumb>

            <Heading class="mb-8" tag="h2">{"Assignment for " + categories[data.category_id - 1].name}</Heading>
        </div>

        <Button disabled={!ongoing}
                on:click={() => saveAssignment()}
                outline
        >
            {ongoing ? "Save Assignment" : "Assignment finished"}
        </Button>


    </div>

    {#if ongoing}
        <Reviews
                show_reviewer=true
                select_reviewer=true
                show_paper=true
        >
            {#each papers.filter((p) => p.category === categories[data.category_id - 1]) as p}
                {#each reviews.filter((r) => r.paper === p) as r}
                    <Review
                            href="/categories/{r.paper.category.id}/{r.paper.id}/{r.id}"
                            id={r.id}
                            paper={r.paper}
                            assignmentOngoing={ongoing}
                    />
                {/each}
            {/each}
        </Reviews>

    {:else}
        <Reviews
                show_paper=true
                show_reviewer=true
                show_review=true
        >
            {#each papers.filter((p) => p.category === categories[data.category_id - 1]) as p}
                {#each reviews.filter((r) => r.paper === p) as r}
                    <Review
                            href="/categories/{r.paper.category.id}/{r.paper.id}/{r.id}"
                            id={r.id}
                            paper={r.paper}
                            assignmentOngoing={ongoing}
                            reviewer={users.find(user => user.id === r.reviewer_id).name}
                    />
                {/each}
            {/each}
        </Reviews>
    {/if}


</Container>
