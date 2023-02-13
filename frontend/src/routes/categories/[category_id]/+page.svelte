<script>
    import {BreadcrumbItem, Button, Heading, Secondary} from "flowbite-svelte";
    import Papers from "../../../components/Papers.svelte";
    import Paper from "../../../components/Paper.svelte";
    import ResponsiveBreadCrumb from "../../../components/ResponsiveBreadCrumb.svelte";
    import Container from "../../../components/Container.svelte";
    import ExternAssignReviewerModal from "../../../components/ExternAssignReviewerModal.svelte";
    import EditModal from "../../../components/EditModal.svelte";
    import ConfirmDeletionModal from "../../../components/ConfirmDeletionModal.svelte";
    import Error from "../../../components/Error.svelte";
    import {page} from '$app/stores';
    import CreateEntryModal from "../../../components/CreateEntryModal.svelte";

    import {onMount} from "svelte";
    import Cookies from "js-cookie";
    import {goto} from "$app/navigation";
    import PaginationComponent from "../../../components/PaginationComponent.svelte";

    function map_type(type) {
        switch (type) {
            case "INTERNAL":
                return "Internal";
            case "EXTERNAL":
                return "External";
        }
    }

    function map_deadline(deadline) {
        if (deadline != null) {
            return new Date(Date.parse(deadline)).toLocaleDateString();
        } else {
            return "not specified"
        }
    }

    /** @type {import("./$types").PageData} */
    export let data;

    let show_assign_modal = false;
    let show_edit_modal = false;
    let show_confirm_deletion_modal = false;
    let show_create_entry_modal = false;

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
        deadline: "",
        score_step_size: "",
        min_score: "",
        max_score: ""
    };

    let current_user = {
        first_name: "",
        last_name: "",
        email: "",
        account_management_url: "",
        id: null
    };

    let entries = null;
    const loading_lines = 5;
    export let error = null;

    function previous() {
        if (currentPage > 1) {
            currentPage -= 1;
            $page.url.searchParams.set("page", currentPage);
            goto($page.url)
            loadEntries()
        }
    }

    function next() {
        if (currentPage < lastPage) {
            currentPage += 1;
            $page.url.searchParams.set("page", currentPage);
            goto($page.url)
            loadEntries()
        }
    }

    let currentPage = 1;
    let lastPage = 1;
    let limit = 1;
    let path = $page.url.pathname;

    function loadCategory() {
        category = null;
        fetch("/api" + path)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    category = resp;
                }
            })
            .catch(err => console.log(err))
    }

    function loadEntries() {
        entries = null;
        currentPage = parseInt(($page.url.searchParams.get("page") ?? 1).toString())
        limit = parseInt(($page.url.searchParams.get("limit") ?? 100).toString())
        fetch("/api" + path + "/entries?page=" + currentPage + "&limit=" + limit)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    lastPage = resp.last_page;
                    entries = resp.content;

                    return fetch("/api/requests", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(entries.map(e => e.id))
                    }).then(resp => resp.json())
                        .then(resp => {
                            for (let i = 0; i < entries.length; i++) {
                                for (const e of resp) {
                                    if (entries[i].id === e.direct_request_process_id) {
                                        entries[i].isReviewer = true;
                                        break;
                                    }
                                }
                            }
                        })
                }
            })
            .catch(err => console.log(err))
    }


    onMount(() => {
        loadCategory()
        loadEntries()
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
    });

    $: if (!show_edit_modal) {
        loadCategory()
    }

    $: if (!show_create_entry_modal) {
        loadEntries()
    }
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
                    <div class="flex flex-row">
                        <Heading tag="h2">
                            {category.name} {category.year}
                        </Heading>
                    </div>
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

                <Button class="mb-4 h-8" color="primary" on:click={() => show_create_entry_modal = true} size="xs">
                    Submit
                    Paper
                </Button>
            </div>


            <Papers
                    category_type={map_type(category.label)}
                    show_slots="true"
            >
                {#if entries === null}
                    {#each [...Array(loading_lines).keys()] as i}
                        <Paper loading="true"/>
                    {/each}
                {:else }
                    {#each entries as e}
                        <Paper
                                on:claimSlot={() => loadEntries()}
                                show_slots=true
                                href="/categories/{category.id}/entries/{e.id}"
                                bind:paper={e}
                                category={category}
                                current_user={current_user}
                                isReviewer={e.isReviewer}
                        />
                    {/each}
                {/if}
            </Papers>


            <PaginationComponent
                    previous={previous}
                    next={next}
                    bind:currentPage={currentPage}
                    bind:lastPage={lastPage}
            />
        </Container>

        <ExternAssignReviewerModal hide={() => show_assign_modal = false} papers={entries} result={(is_direct_assignment, matches) => {
                             console.log(is_direct_assignment, matches);
                             show_assign_modal = false;
                           }}
                                   show={show_assign_modal}/>

        <EditModal conference={category} urlpath={path} hide="{() => show_edit_modal = false}"
                   show="{show_edit_modal}"/>

        <ConfirmDeletionModal hide="{() => show_confirm_deletion_modal = false}" show="{show_confirm_deletion_modal}"
                              to_delete={path} delete_name="{category.name}"/>

        <CreateEntryModal category={category} entries={entries} show={show_create_entry_modal}
                          hide={() => show_create_entry_modal = false}/>
    {/if}
{/if}


