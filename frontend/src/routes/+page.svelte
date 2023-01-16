<script>

  import { BreadcrumbItem, Heading } from "flowbite-svelte";
  import Container from "../components/Container.svelte";
  import ResponsiveBreadCrumb from "../components/ResponsiveBreadCrumb.svelte";
</script>

<Container>
  <ResponsiveBreadCrumb>
    <BreadcrumbItem home href="/">Home</BreadcrumbItem>
  </ResponsiveBreadCrumb>
  <Heading class="mb-4" tag="h2">Home</Heading>
</Container>
