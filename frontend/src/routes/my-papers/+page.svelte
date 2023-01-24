<script>
    import Container from "../../components/Container.svelte";
    import mock_data from "../../mock_data.js";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import MyPapers from "../../components/MyPapers.svelte";
    import MyPaper from "../../components/MyPaper.svelte";

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


    <MyPapers>
        {#each papers as p}
            {#if user.name === p.researcher}
                <MyPaper bind:myPaper={p}/>
            {/if}
        {/each}
    </MyPapers>


</Container>