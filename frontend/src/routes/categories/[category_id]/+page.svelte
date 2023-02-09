<script>
    import {BreadcrumbItem, Button, ChevronLeft, ChevronRight, Heading, Pagination, Secondary} from "flowbite-svelte";
    import mock_data from "../../../mock_data.js";
    import Papers from "../../../components/Papers.svelte";
    import Paper from "../../../components/Paper.svelte";
    import ResponsiveBreadCrumb from "../../../components/ResponsiveBreadCrumb.svelte";
    import Container from "../../../components/Container.svelte";
    import ExternAssignReviewerModal from "../../../components/ExternAssignReviewerModal.svelte";
    import SubmitPaperModal from "../../../components/SubmitPaperModal.svelte";
    import EditModal from "../../../components/EditModal.svelte";
    import ConfirmDeletionModal from "../../../components/ConfirmDeletionModal.svelte";
    import Error from "../../../components/Error.svelte";
    import {page} from '$app/stores';

    import {onMount} from "svelte";
    import Cookies from "js-cookie";

    const pages = mock_data.pagination;
    const papers = mock_data.papers;
    const biddings = mock_data.bidding;

    function map_type(type) {
        switch (type) {
            case "INTERNAL":
                return "Internal";
            case "EXTERNAL":
                return "External";
        }
    }

    function map_deadline(deadline) {
        return new Date(Date.parse(deadline)).toLocaleString();
    }

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
    let show_confirm_deletion_modal = false;

    /*
    function bidding() {
        return biddings.find(bidding => bidding.category.id === mocks[data.category_id - 1].id);
    }

     */
    let category = {
        id: "",
        researcher_id: "",
        name: "",
        year: "",
        label: "",
        deadline: ""
    };

    let current_user = {
        first_name: "",
        last_name: "",
        email: "",
        account_management_url: "",
        id: null
    };

    const loading_lines = 5;
    export let error = null;


    let currentPage = 1;
    let lastPage = 1;
    let limit = 1;
    let path = $page.url.pathname;

    function loadCategory() {
        category = null;
        fetch("/api/" + path)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    lastPage = resp.last_page;
                    category = resp;
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadCategory()
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
    });

</script>


<svelte:head>
    {#if category === null}
        <title> Loading | PeerRequest</title>
    {:else}
        <title>{category.name} | PeerRequest</title>
    {/if}
</svelte:head>
{#if error !== null}
    <Error error={error}/>
{:else}
    {#if category === null}
        LOADING
    {:else}
        <Container>
            <div class="flex max-md:flex-wrap justify-between items-center mb-4">
                <div>
                    <ResponsiveBreadCrumb>
                        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
                        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
                        <BreadcrumbItem>{category.name}</BreadcrumbItem>
                    </ResponsiveBreadCrumb>
                    <Heading tag="h2">
                        {category.name}
                    </Heading>
                    <Heading tag="h6">
                        <Secondary>Review Deadline: {map_deadline(category.deadline)}</Secondary>
                    </Heading>
                </div>


                <!--Assign reviewer button for External categories TODO discuss with frontend what to do
                {#if category.label === "External"}
                    {#if true}
                        <Button size="lg"
                                color="primary"
                                class="mx-auto lg:m-0"

                                on:click={(mocks[data.category_id - 1].open && (bidding === undefined)) ? (() => show_assign_modal = true) : ("")}
                                href={(mocks[data.category_id - 1].open) ?
                            ((bidding() !== undefined) ?
                            `/categories/${mocks[data.category_id - 1].id}/bidding/${bidding().id}` : "") :
                            `/categories/${mocks[data.category_id - 1].id}/assignment`}
                        >
                            {(mocks[data.category_id - 1].open) ? ((bidding() !== undefined) ?
                                "Go to Bidding" : "Assign Reviewers") : "View Assignment"}
                        </Button>
                    {:else}
                        {#if (mocks[data.category_id - 1].open) && (bidding() !== undefined)}
                            <Button size="lg"
                                    color="primary"
                                    class="mx-auto lg:m-0"
                                    href="/categories/{mocks[data.category_id - 1].id}/bidding/{bidding().id}"
                            >
                                Go to Bidding
                            </Button>

                        {/if}
                    {/if}
                {/if}
            -->
            </div>

            <div class="w-full flex justify-between">
                <div class="justify-start gap-x-4 flex">
                    {#if map_type(category.label) === "Internal" ||
                    map_type(category.label) === "External" && category.researcher_id === current_user.id}
                        <Button class="mx-auto lg:m-0 h-8" size="xs" outline on:click={() => show_edit_modal = true}>
                            Edit Conference
                        </Button>
                        <Button class="mx-auto lg:m-0 h-8" color="red" size="xs" outline
                                on:click={() => show_confirm_deletion_modal = true}>
                            Delete Conference
                        </Button>
                    {/if}
                </div>

                <Button class="mb-4 h-8" color="primary" on:click={() => show_submit_modal = true} size="xs">Submit Paper
                </Button>
            </div>


            <Papers
                    category_type={map_type(category.label)}
                    show_category=true
                    show_slots=true
            >
                {#each papers as p}
                    {#if p.category === category}
                        <Paper
                                href="/categories/{p.category.id}/{p.id}"
                                paper={p}
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

        <SubmitPaperModal category_path ={path} conference_type="{map_type(category.label)}" hide="{() => show_submit_modal = false}"
                          show="{show_submit_modal}"/>

        <EditModal conference={path} hide="{() => show_edit_modal = false}"
                   show="{show_edit_modal}"/>

        <ConfirmDeletionModal hide="{() => show_confirm_deletion_modal = false}" show="{show_confirm_deletion_modal}"
                              to_delete={path}/>
    {/if}
{/if}


