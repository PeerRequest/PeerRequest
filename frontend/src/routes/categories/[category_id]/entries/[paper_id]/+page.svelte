<script>
    import {Badge, BreadcrumbItem, Heading} from "flowbite-svelte";
    import mock_data from "../../../../../mock_data.js";
    import Container from "../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../components/ResponsiveBreadCrumb.svelte";
    import PaperView from "../../../../../components/PaperView.svelte";
    import Reviews from "../../../../../components/Reviews.svelte";
    import Review from "../../../../../components/Review.svelte";
    import Error from "../../../../../components/Error.svelte";
    import {page} from '$app/stores';
    import {onMount} from "svelte";


    const mocks_reviews = mock_data.reviews;

    /** @type {import("./$types").PageData} */
    export let data;
    export let document = "/lorem_ipsum.pdf";

    export let error = null;

    let entry = null;
    let category = null;
    let path = $page.url.pathname;

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



    onMount(() => {
        loadEntry()
        loadCategory()
        loadEntryDocument()
    });
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
                    {#if category=== null}
                        LOADING
                    {:else }
                        {category.name}
                    {/if}
                </BreadcrumbItem>
                <BreadcrumbItem>{entry.name}</BreadcrumbItem>
            </ResponsiveBreadCrumb>
            <Heading class="mb-4 flex items-center" tag="h2">{entry.name}
                <Badge class="text-lg font-semibold ml-2"><a href={document} rel="noreferrer" target="_blank">Download</a>
                </Badge>
            </Heading>

            <div class="flex h-full align-items-flex-start">
                <div class="sm:h-full lg:w-[100%] md:w-[100%] mr-4">
                    <PaperView document={document}/>
                </div>

                <div class="lg:w-[50%] md:w-[100%]  mt-7">
                    <Reviews>
                        {#each mocks_reviews.filter((r) => r.paper === entry) as r}
                            <Review
                                    href="/categories/{r.paper.category.id}/entries/{r.paper.id}/{r.id}"
                                    id={r.id}
                            />
                        {/each}
                    </Reviews>
                </div>
            </div>

        </Container>
    {/if}
{/if}