<script>
    import {BreadcrumbItem, Button, ChevronLeft, ChevronRight, Heading, Pagination, Secondary} from "flowbite-svelte";
    import mock_data from "../../../../../mock_data.js";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import Paper from "../../../../../components/Paper.svelte";
    import Papers from "../../../../../components/Papers.svelte";


    const pages = mock_data.pagination;

    const bidding = mock_data.bidding;
    const categories = mock_data.categories;
    const papers = mock_data.papers;

    let rating = 0;
    let biddingOngoing = false;

    /** @type {import("./$types").PageData} */
    export let data;


    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };

    function handleClick() {
        biddingOngoing = !biddingOngoing;
    }
</script>

<svelte:head>
    <title>{"Bidding for " + categories[data.category_id - 1].name} | PeerRequest</title>
</svelte:head>

<Container>
    <div class="flex max-md:flex-wrap justify-between items-center mb-4">
        <div>
            <ResponsiveBreadCrumb>
                <BreadcrumbItem home href="/">Home</BreadcrumbItem>
                <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
                <BreadcrumbItem
                        href="/categories/{categories[data.category_id - 1].id}">{categories[data.category_id - 1].name}</BreadcrumbItem>
                <BreadcrumbItem>Bidding</BreadcrumbItem>

            </ResponsiveBreadCrumb>
            <Heading tag="h2">{"Bidding for " + categories[data.category_id - 1].name}</Heading>
            <Heading class="mb-5" tag="h6">
                <Secondary>{"Review Deadline: " + categories[data.category_id - 1].deadline }</Secondary>
            </Heading>
        </div>

        <div class="mr-5">
            <Button on:click={handleClick}>
                {biddingOngoing ? "Start bidding" : "Stop bidding"}
            </Button>

            <Button disabled={!biddingOngoing}
                    href="../assignment"
                    outline>
                Get results
            </Button>
        </div>

    </div>

    <Papers show_rating=true>
        {#each papers as p}
            {#if p.category === bidding[data.bidding_id - 1].category}
                <Paper
                        href="/categories/{p.category.id}/{p.id}"
                        paper={p}
                        rating={rating}
                />
            {/if}
        {/each}
    </Papers>

    <div class="mx-auto m-8">
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
