<script>
    import {BreadcrumbItem, Button, Heading} from "flowbite-svelte";
    import Container from "../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Category from "../../components/Category.svelte";
    import Categories from "../../components/Categories.svelte";
    import CreateCategoryModal from "../../components/CreateCategoryModal.svelte";
    import {onMount} from "svelte";
    import {page} from '$app/stores';
    import {goto} from "$app/navigation";
    import Error from "../../components/Error.svelte";
    import PaginationComponent from "../../components/PaginationComponent.svelte";

    let show_create_category_modal = false;

    let categories = null;
    const loading_lines = 5;
    export let error = null;

    function previous() {
        if (currentPage > 1) {
            currentPage -= 1;
            $page.url.searchParams.set("page", currentPage);
            goto($page.url)
            loadCategories()
        }
    }

    function next() {
        if (currentPage < lastPage) {
            currentPage += 1;
            $page.url.searchParams.set("page", currentPage);
            goto($page.url)
            loadCategories()
        }
    }

    let currentPage = 1;
    let lastPage = 1;
    let limit = 1;

    function loadCategories() {
        categories = null;
        currentPage = parseInt(($page.url.searchParams.get("page") ?? 1).toString())
        limit = parseInt(($page.url.searchParams.get("limit") ?? 100).toString())
        fetch("/api/categories?page=" + currentPage + "&limit=" + limit)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    lastPage = resp.last_page;
                    categories = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadCategories()
    });

    $: if (!show_create_category_modal) {
        loadCategories();
    }
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
        <Button aria-label="create-new-conference"
                class="mb-4"
                outline
                on:click={() => {show_create_category_modal = true}}
                size="xs">
            Create new Conference
        </Button>
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


        <PaginationComponent
                previous={previous}
                next={next}
                bind:currentPage={currentPage}
                bind:lastPage={lastPage}
        />
    </Container>
{/if}

<CreateCategoryModal existing_categories="{categories}"
                     hide="{() => show_create_category_modal = false}"
                     show="{show_create_category_modal}"/>
