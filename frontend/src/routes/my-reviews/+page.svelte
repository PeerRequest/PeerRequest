<script>
    import Container from "../../components/Container.svelte";
    import mock_data from "../../mock_data.js";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import MyReviews from "../../components/MyReviews.svelte";
    import MyReview from "../../components/MyReview.svelte";
    const user = mock_data.users[23];
    const papers = mock_data.papers;
    const reviews = mock_data.reviews;
</script>

<svelte:head>
    <title>My Reviews | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/my-reviews">My Reviews</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">My Reviews</Heading>


    <MyReviews>
        {#each reviews as r}
            {#if user.name === r.reviewer}
                <MyReview bind:myReview={r}/>
            {/if}
        {/each}
    </MyReviews>


</Container>