<script>
    import { BreadcrumbItem, Button, ChevronLeft, ChevronRight, Heading, Pagination } from "flowbite-svelte";
    import mock_data from "../../mock_data.js";
    import Container from "../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Category from "../../components/Category.svelte";
    import Categories from "../../components/Categories.svelte";
    import CreateCategoryModal from "../../components/CreateCategoryModal.svelte";

    const pages = mock_data.pagination;

    const previous = () => {
        alert("Previous btn clicked. Make a call to your server to fetch data.");
    };
    const next = () => {
        alert("Next btn clicked. Make a call to your server to fetch data.");
    };
    let show_create_category_modal = false;
</script>

<svelte:head>
    <title>Conferences / Journals | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">Conferences</Heading>
    <Button size="lg" class="mx-auto h-8 mb-4" outline
            on:click={() => show_create_category_modal = true}>Create new Conference
    </Button>
    <Categories>
        {#each mock_data.categories as c }
            <Category bind:category={c} />
        {/each}
    </Categories>

    <div class="mx-auto m-8">
        <Pagination icon on:next={next} on:previous={previous} {pages}>
            <svelte:fragment slot="prev">
                <span class="sr-only">Previous</span>
                <ChevronLeft class="w-5 h-5" />
            </svelte:fragment>
            <svelte:fragment slot="next">
                <span class="sr-only">Next</span>
                <ChevronRight class="w-5 h-5" />
            </svelte:fragment>
        </Pagination>
    </div>
</Container>

<CreateCategoryModal hide="{() => show_create_category_modal = false}"
                   show="{show_create_category_modal}"/>
