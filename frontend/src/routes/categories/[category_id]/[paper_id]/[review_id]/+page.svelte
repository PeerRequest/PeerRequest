<script>
    import {Badge, BreadcrumbItem, Heading} from "flowbite-svelte";
    import mock_data from "../../../../../mock_data.js";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import ReviewView from "../../../../../components/ReviewView.svelte";

    const mocks_papers = mock_data.papers;
    const mocks_categories = mock_data.categories;
    const mocks_reviews = mock_data.reviews;

    /** @type {import("./$types").PageData} */
    export let data;
</script>

<svelte:head>
    <title>{mocks_papers[data.paper_id - 1].title} | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
        <BreadcrumbItem
                href="/categories/{data.category_id}">{mocks_categories[data.category_id - 1].name}</BreadcrumbItem>
        <BreadcrumbItem>{mocks_papers[data.paper_id - 1].title}</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4 flex items-center" tag="h2">{mocks_papers[data.paper_id - 1].title}
    </Heading>

    <ReviewView/>


    <!-- CONTENT HERE -->
</Container>
