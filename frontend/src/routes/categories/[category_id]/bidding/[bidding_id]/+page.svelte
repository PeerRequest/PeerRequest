<script>
    import {BreadcrumbItem, ChevronLeft, ChevronRight, Heading, Pagination, Secondary} from "flowbite-svelte";
    import mock_data from "../../../../../mock_data.js";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import Biddings from "../../../../../components/Biddings.svelte";
    import Bidding from "../../../../../components/Bidding.svelte";


    const pages = mock_data.pagination;

    // my stuff start
    const biddingElements = mock_data.bidding[0];
    const categories = mock_data.categories;
    const papers = mock_data.papers;
    // my stuff end



    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };
</script>

<svelte:head>
    <title>Conferences / Journals | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
        <BreadcrumbItem>TestTest</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">Just A Bidding Process</Heading>
    <Heading tag="h6">
        <Secondary> Owner: TODO </Secondary>
    </Heading>
    <Heading class="mb-5" tag="h6">
        <Secondary>Review Deadline: TODO</Secondary>
    </Heading>

    <Biddings>
        {#each papers as p}
            {#if p.category.id === biddingElements.category.id}
                <Bidding
                        title={p.title}
                        href="/categories/{p.category.id}/{p.id}"
                />
            {/if}
        {/each}
    </Biddings>

    <div class="mx-auto m-8">
        <Pagination icon on:next={next} on:previous={previous} {pages}>
            <svelte:fragment slot="prev">
                <span class="sr-only">Previous</span>
                <ChevronLeft class="w-5 h-5" />
            </svelte:fragment>
            <svelte:fragment slot="next">
                <span class="sr-only">Next</span>
                <ChevronRight class="w-5 h-5" />
            </svelte:fragment>
        </Pagination>
    </div>
</Container>
