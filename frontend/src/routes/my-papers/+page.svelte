<script>
    import Container from "../../components/Container.svelte";
    import mock_data from "../../mock_data.js";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Papers from "../../components/Papers.svelte";
    import Paper from "../../components/Paper.svelte";


    const user = mock_data.users[23];
    const papers = mock_data.papers;
</script>

<svelte:head>
    <title>My Papers | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/my-papers">My Papers</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">My Papers</Heading>

    <Papers>
        {#each papers as p}
            {#if user.name === p.researcher}
                <Paper
                        href="/categories/{p.category.id}/{p.id}"
                        title={p.title}
                        category={p.category}
                        show_category=true
                />
            {/if}
        {/each}
    </Papers>


</Container>