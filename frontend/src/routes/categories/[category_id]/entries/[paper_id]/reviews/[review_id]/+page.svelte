<script>
    import {BreadcrumbItem, Heading, Secondary} from "flowbite-svelte";
    import Container from "../../../../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../../../../components/ResponsiveBreadCrumb.svelte";
    import ReviewView from "../../../../../../../components/ReviewView.svelte";
    import {page} from '$app/stores';
    import {onMount} from "svelte";
    import Error from "../../../../../../../components/Error.svelte";


    /** @type {import("./$types").PageData} */
    export let data;

    export let error = null;

    let entry = null;
    let category = {
        "min_score": 0.5,
        "max_score": 5,
        "score_step_size": 0.25
    }
    let review = null
    //TODO UserController
    let reviewer = null;
    let path = $page.url.pathname;

    function loadEntry() {
        entry = null;
        fetch("/api/categories/" + data.category_id + "/entries/" + data.paper_id)
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

    function loadReview() {
        review = null;
        fetch("/api" + path)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    review = resp;
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadEntry();
        loadCategory();
        loadReview();
    });
</script>


<svelte:head>
    {#if review === null}
        <title> Loading | PeerRequest</title>
    {:else}
        <title> Review#{review.id} | PeerRequest</title>
    {/if}
</svelte:head>

{#if error !== null}
    <Error error={error}/>
{:else}
    {#if review !== null}
        <Container>
            <ResponsiveBreadCrumb>
                <BreadcrumbItem home href="/">Home</BreadcrumbItem>
                <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
                <BreadcrumbItem
                        href="/categories/{data.category_id}">{category.name}</BreadcrumbItem>
                <BreadcrumbItem
                        href="/categories/{data.category_id}/entries/{data.paper_id}">
                    {#if entry === null}
                        Loading entry name
                    {:else }
                        {entry.name}
                    {/if}
                </BreadcrumbItem>
                <BreadcrumbItem>{"Review #" + String(review.id)}</BreadcrumbItem>
            </ResponsiveBreadCrumb>

            <Heading class="mb-1 flex items-center" tag="h2">
                {"Review #" + String(review.id)}
            </Heading>
            <Heading tag="h6">
                <Secondary>
                    {#if reviewer !== null}
                        Reviewer: {reviewer.first_name +" "+ reviewer.last_name}
                    {:else}
                        Loading Reviewer
                    {/if}
                </Secondary>
            </Heading>
            <ReviewView review="{review}" category="{category}"/>
        </Container>
    {/if}
{/if}