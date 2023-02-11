<script>
    import {Badge, Button, BreadcrumbItem, Heading} from "flowbite-svelte";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import PaperView from "../../../../../components/PaperView.svelte";
    import Reviews from "../../../../../components/Reviews.svelte";
    import Review from "../../../../../components/Review.svelte";
    import Error from "../../../../../components/Error.svelte";
    import {page} from '$app/stores';
    import {onMount} from "svelte";
    import EditModal from "../../../../../components/EditModal.svelte";
    import ConfirmDeletionModal from "../../../../../components/ConfirmDeletionModal.svelte";

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

    const loading_lines = 5;
    let currentPage = 1;
    let lastPage = 1;
    let limit = 1;

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

    onMount(() => {
        loadEntry()
        loadCategory()
        loadEntryDocument()
        loadReviews()
    });

    let show_edit_modal = false;

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
            <Heading class="mb-4 flex items-center" tag="h2">{entry.name}
                <Badge class="text-lg font-semibold ml-2"><a href={document} rel="noreferrer"
                                                             target="_blank">Download</a>
                </Badge>
            </Heading>


            <div class="justify-start gap-x-4 flex">
                <Button class="mx-auto lg:m-0 h-8" size="xs" outline on:click={() => show_edit_modal = true}>
                    Edit Paper
                </Button>
                <Button class="mx-auto lg:m-0 h-8" color="red" size="xs" outline
                        on:click={() => show_confirm_deletion_modal = true}>
                    Delete Paper
                </Button>
            </div>

            <div class="flex h-full align-items-flex-start">
                <div class="sm:h-full lg:w-[100%] md:w-[100%] mr-4">
                    <PaperView document={document}/>
                </div>

                <div class="lg:w-[50%] md:w-[100%]  mt-7">
                    <Reviews show_reviewer=true>
                        {#if reviews === null}
                            {#each [...Array(loading_lines).keys()] as i}
                                <Review loading="true"/>
                            {/each}
                        {:else }
                            {#each reviews as r}
                                <Review
                                        show_reviewer=true
                                        bind:review={r}
                                        category={category}
                                        paper={entry}
                                />
                            {/each}
                        {/if}
                    </Reviews>
                </div>
            </div>

        </Container>

        <EditModal paper={entry} urlpath={path} hide="{() => show_edit_modal = false}"
                   show="{show_edit_modal}"/>

        <ConfirmDeletionModal hide="{() => show_confirm_deletion_modal = false}" show="{show_confirm_deletion_modal}"
                              to_delete={path} delete_name="{entry.name}" afterpath="{go_after}"/>
    {/if}
{/if}