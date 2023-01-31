<script>
    import {Badge, BreadcrumbItem, Heading} from "flowbite-svelte";
    import mock_data from "../../../../mock_data.js";
    import Container from "../../../../components/Container.svelte";
    import ResponsiveBreadCrumb from "../../../../components/ResponsiveBreadCrumb.svelte";
    import PaperView from "../../../../components/PaperView.svelte";
    import Reviews from "../../../../components/Reviews.svelte";
    import Review from "../../../../components/Review.svelte";

    const mocks_papers = mock_data.papers;
    const mocks_categories = mock_data.categories;
    const mocks_reviews = mock_data.reviews;

    /** @type {import("./$types").PageData} */
    export let data;
    export let document = "/lorem_ipsum.pdf";
</script>

<svelte:head>
    <title>{mocks_papers[data.paper_id - 1].title} | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/categories">Conferences</BreadcrumbItem>
        <BreadcrumbItem
                href="/categories/{data.category_id}">{mocks_categories[data.category_id - 1].name}</BreadcrumbItem>
        <BreadcrumbItem>{mocks_papers[data.paper_id - 1].title}</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4 flex items-center" tag="h2">{mocks_papers[data.paper_id - 1].title}
        <Badge class="text-lg font-semibold ml-2"><a href={document} rel="noreferrer" target="_blank">Download</a>
        </Badge>
    </Heading>

    <div class="flex h-full align-items-flex-start">
        <div class="sm:h-full lg:w-[100%] md:w-[100%] mr-4">
            <PaperView document={document}/>
        </div>

        <div class="lg:w-[50%] md:w-[100%]  mt-7">
            <Reviews>
                {#each mocks_reviews.filter((r) => r.paper === mocks_papers[data.paper_id - 1]) as r}
                    <Review
                            href="/categories/{r.paper.category.id}/{r.paper.id}/{r.id}"
                            id={r.id}
                    />
                {/each}
            </Reviews>
        </div>
    </div>


    <!-- CONTENT HERE -->
</Container>
