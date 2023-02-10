<script>
    import {BreadcrumbItem, Heading, Secondary} from "flowbite-svelte";
    import mock_data from "../../../../../../../mock_data.js";
    import Container from "../../../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../../../components/ResponsiveBreadCrumb.svelte";
    import ReviewView from "../../../../../../../components/ReviewView.svelte";

    const mocks_papers = mock_data.papers;
    const mocks_categories = mock_data.categories;
    const mocks_reviews = mock_data.reviews;

    /** @type {import("./$types").PageData} */
    export let data;
</script>


<svelte:head>
    <title>{"Review #" + String(mocks_reviews[data.review_id - 1].id)} | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
        <BreadcrumbItem
                href="/categories/{data.category_id}">{mocks_categories[data.category_id - 1].name}</BreadcrumbItem>
        <BreadcrumbItem
                href="/categories/{data.category_id}/{data.paper_id}">{mocks_papers[data.paper_id - 1].title}</BreadcrumbItem>
        <BreadcrumbItem>{"Review #" + String(mocks_reviews[data.review_id - 1].id)}</BreadcrumbItem>
    </ResponsiveBreadCrumb>

    <Heading class="mb-1 flex items-center" tag="h2">
        {"Review #" + String(mocks_reviews[data.review_id - 1].id)}
    </Heading>
    <Heading tag="h6">
        <Secondary> Reviewer: {mocks_reviews[data.review_id - 1].reviewer}</Secondary>
    </Heading>

    <ReviewView/>

</Container>
