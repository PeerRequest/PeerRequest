<script>
    import {BreadcrumbItem, Button, ChevronLeft, ChevronRight, Heading, Pagination, Secondary} from "flowbite-svelte";
    import mock_data from "../../../../../mock_data.js";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import Paper from "../../../../../components/Paper.svelte";
    import Papers from "../../../../../components/Papers.svelte";
    import ConfirmDeletionModal from "../../../../../components/ConfirmDeletionModal.svelte";
    import EditModal from "../../../../../components/EditModal.svelte";

    const pages = mock_data.pagination;
    const bidding = mock_data.bidding;
    const categories = mock_data.categories;
    const papers = mock_data.papers;

    let rating = 0;
    let biddingOngoing = true;

    /** @type {import("./$types").PageData} */
    export let data;


    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };

    let show_edit_modal = false;
    let show_confirm_deletion_modal = false;

</script>

<svelte:head>
    <title>{"Bidding for " + categories[data.category_id - 1].name} | PeerRequest</title>
</svelte:head>

<Container>
    <div class="flex max-md:flex-wrap justify-between items-center">
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
                <Secondary>{"Review Deadline: " + bidding[data.bidding_id - 1].deadline }</Secondary>
            </Heading>
        </div>

        {#if categories[data.category_id - 1].is_my_category()}
            <div class="mr-5">
                <Button color="primary"
                        on:click={() => {biddingOngoing = false; categories[data.category_id - 1].open = false}}
                        disabled={!biddingOngoing}
                        href="/categories/{categories[data.category_id - 1].id}/assignment"
                >
                    Stop Bidding
                </Button>

                <Button color="primary"
                        disabled={!biddingOngoing}>
                    Refresh Bidding
                </Button>
            </div>
        {/if}

    </div>


    <div class="-mt-2 mb-5">
        {#if categories[data.category_id - 1].is_my_category()}
            <Button size="xs"
                    disabled={!biddingOngoing}
                    outline
                    on:click={() => show_edit_modal=true}
            >
                Edit Deadline
            </Button>

            <Button size="xs"
                    color="red"
                    outline
                    on:click={() => show_confirm_deletion_modal=true}>
                Delete Bidding
            </Button>

        {:else}
            <Button color="red" size="xs" outline>
                Decline Bidding
            </Button>
        {/if}
    </div>


    {#if categories[data.category_id - 1].is_my_category()}
        <Papers>
            {#each papers as p}
                {#if p.category === bidding[data.bidding_id - 1].category}
                    <Paper
                            href="/categories/{p.category.id}/{p.id}"
                            paper={p}
                    />
                {/if}
            {/each}
        </Papers>
    {:else}
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

    {/if}

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

<EditModal bidding={bidding[data.bidding_id -1]}
                   hide={() => show_edit_modal = false}
                   show={show_edit_modal}
/>

<ConfirmDeletionModal hide={() => show_confirm_deletion_modal = false}
                      show={show_confirm_deletion_modal}
                      to_delete={categories[data.category_id - 1]}
/>
