<script>
  import { Badge, BreadcrumbItem, Heading } from "flowbite-svelte";
  import mock_data from "../../../../mock_data.js";
  import Container from "../../../../components/Container.svelte";
  import ResponsiveBreadCrumb from "../../../../components/ResponsiveBreadCrumb.svelte";
  import PaperView from "../../../../components/PaperView.svelte";

  const mocks_papers = mock_data.papers;
  const mocks_categories = mock_data.categories;

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
    <BreadcrumbItem href="/categories/{data.category_id}">{mocks_categories[data.category_id - 1].name}</BreadcrumbItem>
    <BreadcrumbItem>{mocks_papers[data.paper_id - 1].title}</BreadcrumbItem>
  </ResponsiveBreadCrumb>
  <Heading class="mb-4 flex items-center" tag="h2">{mocks_papers[data.paper_id - 1].title}
    <Badge class="text-lg font-semibold ml-2"><a href={document} rel="noreferrer" target="_blank">Download</a></Badge>
  </Heading>

  <PaperView document={document} />

  <!-- CONTENT HERE -->
</Container>
