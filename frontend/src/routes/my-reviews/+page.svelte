<script>
    import Container from "../../components/Container.svelte";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Reviews from "../../components/Reviews.svelte";
    import Review from "../../components/Review.svelte";
    import {onMount} from "svelte";

    export let error;
    let reviews = null;
    let entries = null;

    function loadUserReviews() {
        reviews = null;
        fetch("/api/reviews")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    reviews = resp;
                }
            })
            .catch(err => console.log(err))
    }

    function loadUserEntries() {
        entries = null;
        fetch("/api/entries")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    entries = resp;
                }
            })
            .catch(err => console.log(err))
    }
    function findEntry(entry_id) {
        let test = null
        if (entries !== null) {
            entries.forEach( function (item) {
                if (item.id === entry_id) {
                    test = item
                    return item
                }
            })
        }
        return test
    }

    onMount(() => {
        loadUserReviews();
        loadUserEntries();
    });

</script>

<svelte:head>
    <title>My Reviews | PeerRequest</title>
</svelte:head>


{#if reviews === null || entries===null}
    LOADING
{:else}
    <Container>
        <ResponsiveBreadCrumb>
            <BreadcrumbItem home href="/">Home</BreadcrumbItem>
            <BreadcrumbItem href="/my-reviews">My Reviews</BreadcrumbItem>
        </ResponsiveBreadCrumb>
        <Heading class="mb-4" tag="h2">My Reviews</Heading>


        <Reviews
                show_category=true
                show_paper=true>
            {#each reviews as r}
                <Review
                        href="/categories/{findEntry(r.entry_id).category_id}/entries/{r.entry_id}/reviews/{r.id}"
                        bind:review={r}
                        paper={findEntry(r.entry_id)}
                        show_paper=true
                        show_category=true
                        category_id={findEntry(r.entry_id).category_id}
                />
            {/each}
        </Reviews>

    </Container>
{/if}