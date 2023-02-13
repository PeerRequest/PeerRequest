<script>
    import Container from "../../components/Container.svelte";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Reviews from "../../components/Reviews.svelte";
    import Review from "../../components/Review.svelte";
    import {onMount} from "svelte";

    export let error;
    let reviews = null;

    function loadUserReviews() {
        reviews = null;
        fetch("/api/reviews")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    reviews = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadUserReviews();
    });

</script>

<svelte:head>
    <title>My Reviews | PeerRequest</title>
</svelte:head>


{#if reviews === null}
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
                        href="/categories/{r.second.category_id}/entries/{r.second.id}/reviews/{r.first.id}"
                        bind:review={r.first}
                        paper={r.second}
                        show_paper=true
                        show_category=true
                        category_id={r.second.category_id}
                />
            {/each}
        </Reviews>

    </Container>
{/if}