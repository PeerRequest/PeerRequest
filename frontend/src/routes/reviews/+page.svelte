<script>
    import { Breadcrumb, BreadcrumbItem, ChevronLeft, ChevronRight, Heading, Pagination } from "flowbite-svelte";
    import mock_data from "../../mock_data.js";
    import Container from "../../components/Container.svelte";

    const pages = mock_data.pagination;

    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };
</script>

<svelte:head>
    <title>Reviews | PeerRequest</title>
</svelte:head>

<Container>
    <Breadcrumb class="mb-4">
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/reviews">Reviews</BreadcrumbItem>
    </Breadcrumb>
    <Heading class="mb-4" tag="h2">Reviews</Heading>

    <!-- CONTENT HERE -->

    <div class="mx-auto mb-8">
        <Pagination icon on:next={next} on:previous={previous} {pages}>
            <svelte:fragment slot="prev">
                <span class="sr-only">Previous</span>
                <ChevronLeft class="w-5 h-5"/>
            </svelte:fragment>
            <svelte:fragment slot="next">
                <span class="sr-only">Next</span>
                <ChevronRight class="w-5 h-5"/>
            </svelte:fragment>
        </Pagination>
    </div>
</Container>

