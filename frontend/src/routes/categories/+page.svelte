<script>
    import {BreadcrumbItem, ChevronLeft, ChevronRight, Heading, Pagination} from "flowbite-svelte";
    import mock_data from "../../mock_data.js";
    import Container from "../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Category from "../../components/Category.svelte";
    import Categories from "../../components/Categories.svelte";
    import {onMount} from "svelte";
    import Error from "../../components/Error.svelte";

    const pages = mock_data.pagination;

    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };

    let categories = null;
    const loading_lines = 5;
    export let error = null;

    onMount(() => {
        fetch("/api/categories")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    console.log(resp);
                    categories = resp;
                }
            })
            .catch(err => console.log(err))
    });
</script>

<svelte:head>
    <title>Conferences / Journals | PeerRequest</title>
</svelte:head>

{#if error !== null}
    <Error error={error}/>

{:else}
    <Container>
        <ResponsiveBreadCrumb>
            <BreadcrumbItem home href="/">Home</BreadcrumbItem>
            <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
        </ResponsiveBreadCrumb>
        <Heading class="mb-4" tag="h2">Conferences</Heading>

        <Categories>
            {#if categories === null}
                {#each [...Array(loading_lines).keys()] as i}
                    <Category loading="true"/>
                {/each}
            {:else }
                {#each categories as c }
                    <Category bind:category={c}/>
                {/each}
            {/if}
        </Categories>

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
{/if}

