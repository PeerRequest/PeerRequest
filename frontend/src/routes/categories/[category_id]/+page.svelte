<script>
    import {BreadcrumbItem, Button, ChevronLeft, ChevronRight, Heading, Pagination, Secondary} from "flowbite-svelte";
    import mock_data from "../../../mock_data.js";
    import Papers from "../../../components/Papers.svelte";
    import Paper from "../../../components/Paper.svelte";
    import ResponsiveBreadCrumb from "../../../components/ResponsiveBreadCrumb.svelte";
    import Container from "../../../components/Container.svelte";
    import ExternAssignReviewerModal from "../../../components/ExternAssignReviewerModal.svelte";
    import SubmitPaperModal from "../../../components/SubmitPaperModal.svelte";
    import EditCategoryModal from "../../../components/EditCategoryModal.svelte";

    const pages = mock_data.pagination;
    const mocks = mock_data.categories;
    const papers = mock_data.papers;

    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };

    /** @type {import("./$types").PageData} */
    export let data;

    let show_assign_modal = false;
    let show_submit_modal = false;
    let show_edit_modal = false;
</script>

<svelte:head>
    <title>{mocks[data.category_id - 1].name} | PeerRequest</title>
</svelte:head>

<Container>
    <div class="flex max-md:flex-wrap justify-between items-center mb-4">
        <div>
            <ResponsiveBreadCrumb>
                <BreadcrumbItem home href="/">Home</BreadcrumbItem>
                <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
                <BreadcrumbItem>{mocks[data.category_id - 1].name}</BreadcrumbItem>
            </ResponsiveBreadCrumb>
            <Heading tag="h2">
                {mocks[data.category_id - 1].name}
                {#if mocks[data.category_id - 1].type === "Internal" ||
                mocks[data.category_id - 1].type === "External" && mocks[data.category_id - 1].is_my_category()}
                    <Button class="mx-auto lg:m-0" outline on:click={() => show_edit_modal = true}>
                        Edit
                    </Button>
                {/if}
            </Heading>
            <Heading tag="h6">
                <Secondary>Review Deadline: {mocks[data.category_id - 1].deadline}</Secondary>
            </Heading>
        </div>

        {#if mocks[data.category_id - 1].type === "External" && mocks[data.category_id - 1].is_my_category() }
            <Button size="lg" color="primary" class="mx-auto lg:m-0"
                    on:click={() => show_assign_modal = true}>Assign Reviewers
            </Button>
        {/if}
    </div>
    <div class="w-full flex justify-end">
        <Button class="mb-4" color="primary" on:click={() => show_submit_modal = true} size="xs">Submit Paper</Button>
    </div>


    <Papers
            category_type={mocks[data.category_id - 1].type}
            show_category=true
            show_slots=true
    >
        {#each papers as p}
            {#if p.category === mocks[data.category_id - 1]}
                <Paper
                        href="/categories/{p.category.id}/{p.id}"
                        id={p.id}
                        title={p.title}
                        slots={p.slots}
                        category={p.category}
                />
            {/if}
        {/each}
    </Papers>


    <div class="mx-auto my-8">
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


<ExternAssignReviewerModal hide={() => show_assign_modal = false} papers={papers} result={(is_direct_assignment, matches) => {
                             console.log(is_direct_assignment, matches);
                             show_assign_modal = false;
                           }}
                           show={show_assign_modal}/>

<SubmitPaperModal conference_type="{mocks[data.category_id - 1].type}" hide="{() => show_submit_modal = false}"
                  show="{show_submit_modal}"/>

<EditCategoryModal conference="{mocks[data.category_id - 1]}" hide="{() => show_edit_modal = false}"
                  show="{show_edit_modal}"/>