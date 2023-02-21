<script>
    import {Badge, Button, BreadcrumbItem, Heading, Secondary} from "flowbite-svelte";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import PaperView from "../../../../../components/PaperView.svelte";
    import Reviews from "../../../../../components/Reviews.svelte";
    import Review from "../../../../../components/Review.svelte";
    import Error from "../../../../../components/Error.svelte";
    import {page} from '$app/stores';
    import {onMount} from "svelte";
    import {goto} from "$app/navigation";
    import EditModal from "../../../../../components/EditModal.svelte";
    import ConfirmDeletionModal from "../../../../../components/ConfirmDeletionModal.svelte";
    import Cookies from "js-cookie";
    import EditRequestsModal from "../../../../../components/EditRequestsModal.svelte";

    let show_confirm_deletion_modal = false;

    /** @type {import("./$types").PageData} */
    export let data;
    export let document = "/lorem_ipsum.pdf";

    export let error = null;

    let users = null;
    let reviewer = null;
    let entry = null;
    let category = null;
    let reviews = null;
    let path = $page.url.pathname;
    let go_after;
    let user_reviews = null;
    let review_href = null;

    const loading_lines = 5;
    let currentPage = 1;
    let lastPage = 1;
    let limit = 1;
    let current_user = null;

    function loadEntry() {
        entry = null;
        fetch("/api" + path)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    entry = resp;
                    current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
                    loadCategory()
                    loadEntryDocument()
                    if (entry.researcher_id === current_user.id) {
                        loadReviews()
                    }
                    fetch("/api/requests")
                        .then(resp => resp.json())
                        .then(resp => {
                            if (resp.status < 200 || resp.status >= 300) {
                                error = "" + resp.status + ": " + resp.message;
                                console.log(error);
                            } else {
                                for (const e of resp.content) {
                                    if (entry.id === e.second.id) {
                                        loadUserReviews()
                                        break;
                                    }
                                }
                            }
                        })
                }
            })
            .catch(err => console.log(err))
    }

    function loadCategory() {
        category = null;
        fetch("/api/categories/" + data.category_id)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    category = resp;
                    go_after = "/categories/" + category.id;
                }
            })
            .catch(err => console.log(err))
    }

    function loadEntryDocument() {
        document = null;
        fetch("/api" + path + "/paper")
            .then(resp => resp.blob())
            .then(resp => {
                document = window.URL.createObjectURL(resp);
            })
            .catch(err => console.log(err))
    }

    function loadReviews() {
        reviews = null;
        currentPage = parseInt(($page.url.searchParams.get("page") ?? 1).toString())
        limit = parseInt(($page.url.searchParams.get("limit") ?? 100).toString())
        fetch("/api" + path + "/reviews?page=" + currentPage + "&limit=" + limit)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    lastPage = resp.last_page;
                    reviews = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    function loadUserReviews() {
        user_reviews = null;
        fetch("/api/reviews")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    user_reviews = resp.content;
                    user_reviews.forEach(getReview)
                }
            })
            .catch(err => console.log(err))
    }

    function getReview(pair) {
        if (entry.id === pair.second.id) {
            review_href = pair.first.id;
        }
    }

    function map_deadline(deadline) {
        if (deadline != null) {
            return new Date(Date.parse(deadline)).toLocaleDateString();
        } else {
            return "not specified"
        }
    }

    onMount(() => {
        loadEntry()
    });

    let show_edit_modal = false;
    let show_add_reviewer_modal = false

    $: if (!show_edit_modal) {
        loadEntry()
    }
</script>

<svelte:head>
    {#if entry === null}
        <title> Loading | PeerRequest</title>
    {:else}
        <title>{entry.name} | PeerRequest</title>
    {/if}
</svelte:head>

{#if error !== null}
    <Error error={error}/>
{:else}
    {#if (entry === null)}
        LOADING
    {:else}
        <Container>
            <ResponsiveBreadCrumb>
                <BreadcrumbItem home href="/">Home</BreadcrumbItem>
                <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
                <BreadcrumbItem href="/categories/{data.category_id}">
                    {#if category === null}
                        LOADING
                    {:else }
                        {category.name}
                    {/if}
                </BreadcrumbItem>
                <BreadcrumbItem>{entry.name}</BreadcrumbItem>
            </ResponsiveBreadCrumb>
            <div class="flex flex-row w-full justify-between">
                <Heading tag="h2">
                    {entry.name}
                    <Badge class="text-lg font-semibold ml-2"><a href={document} rel="noreferrer"
                                                                 target="_blank" download>Download</a>
                    </Badge>
                </Heading>
                {#if review_href !== null}
                    <Button size="xs" outline on:click|once={() => goto(path + "/reviews/" + review_href)}> Go to your Review</Button>
                {/if}
            </div>
            <Heading tag="h6">

                <Secondary>Review Deadline:
                    {#if category !== null}
                        {map_deadline(category.deadline)}
                    {:else}
                        LOADING
                    {/if}
                </Secondary>
                <Secondary>
                    <div class="w-[50%] overflow-x-auto">
                        {#if entry.authors === "undefined"}
                        {:else }
                            <h1 class="font-extrabold">Authors:</h1> {entry.authors}
                        {/if}
                    </div>
                </Secondary>
            </Heading>


            {#if current_user !== null && current_user.id === entry.researcher_id}
                <div class="w-full flex justify-between mt-4">
                    <div class="justify-start gap-x-4 flex">
                        <Button class="mx-auto my-auto lg:m-0 h-10" size="xs" outline
                                on:click={() => show_edit_modal = true}>
                            Edit Paper
                        </Button>
                        <Button class="mx-auto my-auto lg:m-0 h-10" color="red" size="xs" outline on:click={() => show_confirm_deletion_modal = true}>
                            Delete Paper
                        </Button>
                    </div>

                    <Button class="mx-auto lg:m-0 h-12" size="md" outline on:click={() => show_add_reviewer_modal = true}>
                        Edit Requests and Open Slots
                    </Button>
                </div>
            {/if}

            <div class="flex h-full align-items-flex-start">
                <div class="sm:h-full lg:w-[100%] md:w-[100%] mr-4">
                    <PaperView document={document}/>
                </div>

                <div class="lg:w-[50%] md:w-[100%]  mt-7">
                    {#if reviews !== null}
                        <Reviews show_reviewer=true>
                            {#each reviews as r}
                                <Review
                                        show_reviewer=true
                                        bind:review={r}
                                        category_id={category.id}
                                        paper={entry}
                                />
                            {/each}
                        </Reviews>
                    {/if}
                </div>
            </div>

        </Container>

        <EditModal paper={entry} urlpath={path} hide="{() => show_edit_modal = false}"
                   show="{show_edit_modal}"/>

        <ConfirmDeletionModal hide="{() => show_confirm_deletion_modal = false}" show="{show_confirm_deletion_modal}"
                              to_delete={path} delete_name="{entry.name}" afterpath="{go_after}"/>

        <EditRequestsModal paper={entry} hide="{() => show_add_reviewer_modal = false}"
                          show="{show_add_reviewer_modal}"/>
    {/if}
{/if}